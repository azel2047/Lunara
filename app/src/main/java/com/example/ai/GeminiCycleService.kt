package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "model"
    val text: String,
    val thinkingText: String? = null,
    val searchSources: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

class GeminiCycleService {

    companion object {
        const val DEFAULT_GEMINI_MODEL = "gemini-2.5-flash"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiKey: String = try {
        BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
        ""
    }

    private val systemInstruction = """
        Kamu adalah 'Lunara AI', asisten pakar kesehatan reproduksi wanita dan ritme hormonal yang hangat, ilmiah, berempati, dan suportif.
        Bahasa utama: Bahasa Indonesia yang lembut, sopan, dan mudah dipahami.
        
        PEDOMAN KESELAMATAN MEDIS MUTLAK:
        1. JANGAN PERNAH memberikan diagnosis medis definitif atau meresepkan obat keras / terapi hormon.
        2. Jika pengguna mengeluhkan rasa sakit hebat (kram parah tidak tertahankan, pendarahan abnormal, pingsan, demam tinggi), SELALU anjurkan secara tegas untuk segera memeriksakan diri ke dokter spesialis kebidanan dan kandungan (Sp.OG) atau fasilitas gawat darurat terdekat.
        3. Selalu ingatkan bahwa saran ini bersifat edukatif dan bukan pengganti diagnosis medis resmi.
        4. Berikan tips gaya hidup sehat, asupan nutrisi alami seimbang (seperti zat besi, magnesium, kalsium), hidrasi, serta relaksasi fisik sesuai fase biologis tubuh.
    """.trimIndent()

    suspend fun sendMessage(
        history: List<ChatMessage>,
        userMessage: String,
        useDeepThinking: Boolean = false,
        useSearchGrounding: Boolean = true
    ): ChatMessage = withContext(Dispatchers.IO) {
        // Cek API Key
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ChatMessage(
                sender = "model",
                text = getSimulatedInsightResponse(userMessage)
            )
        }

        // Susun Payload REST API Gemini
        val root = JSONObject()

        // System Instruction
        val sysInstructionObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", systemInstruction))
        sysInstructionObj.put("parts", sysParts)
        root.put("system_instruction", sysInstructionObj)

        // Contents
        val contentsArray = JSONArray()
        history.takeLast(8).forEach { msg ->
            val msgObj = JSONObject()
            msgObj.put("role", msg.sender)
            val parts = JSONArray()
            parts.put(JSONObject().put("text", msg.text))
            msgObj.put("parts", parts)
            contentsArray.put(msgObj)
        }
        val currentMsgObj = JSONObject()
        currentMsgObj.put("role", "user")
        val currentParts = JSONArray()
        currentParts.put(JSONObject().put("text", userMessage))
        currentMsgObj.put("parts", currentParts)
        contentsArray.put(currentMsgObj)
        root.put("contents", contentsArray)

        // Tools (Google Search Grounding opsional)
        if (useSearchGrounding && !useDeepThinking) {
            val toolsArray = JSONArray()
            val googleSearchTool = JSONObject()
            googleSearchTool.put("googleSearch", JSONObject())
            toolsArray.put(googleSearchTool)
            root.put("tools", toolsArray)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$DEFAULT_GEMINI_MODEL:generateContent?key=$apiKey"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = root.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiService", "API Error ${response.code}: $responseBody")
                return@withContext ChatMessage(
                    sender = "model",
                    text = getSimulatedInsightResponse(userMessage)
                )
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")

                var replyText = ""
                var thinkingText: String? = null
                val sources = mutableListOf<String>()

                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("thought") && part.getBoolean("thought")) {
                            thinkingText = part.optString("text")
                        } else if (part.has("text")) {
                            replyText += part.getString("text")
                        }
                    }
                }

                val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
                val searchChunks = groundingMetadata?.optJSONArray("groundingChunks")
                if (searchChunks != null) {
                    for (i in 0 until searchChunks.length()) {
                        val chunk = searchChunks.getJSONObject(i)
                        val web = chunk.optJSONObject("web")
                        val title = web?.optString("title")
                        if (!title.isNullOrBlank()) {
                            sources.add(title)
                        }
                    }
                }

                if (replyText.isBlank()) {
                    replyText = "Tubuhmu sedang beradaptasi dengan ritme hormon hari ini. Pastikan istirahat cukup dan jaga hidrasi teratur ya."
                }

                return@withContext ChatMessage(
                    sender = "model",
                    text = replyText.trim(),
                    thinkingText = thinkingText,
                    searchSources = sources
                )
            } else {
                return@withContext ChatMessage(
                    sender = "model",
                    text = getSimulatedInsightResponse(userMessage)
                )
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Network or connectivity error", e)
            return@withContext ChatMessage(
                sender = "model",
                text = getSimulatedInsightResponse(userMessage)
            )
        }
    }

    private fun getSimulatedInsightResponse(prompt: String): String {
        val lower = prompt.lowercase()
        val disclaimer = "\n\n*(Catatan: Informasi ini bersifat edukatif dan bukan pengganti diagnosis medis dokter.)*"
        return when {
            "makan" in lower || "nutrisi" in lower || "asam" in lower -> {
                "Nutrisi seimbang sangat penting bagi kestabilan ritme tubuhmu. Pada fase ovulasi dan folikuler, tubuh memerlukan mikronutrien seperti magnesium (biji labu, kacang almond, cokelat hitam 70%+), asam folat, dan sayuran hijau tua. Jangan lupa cukupi cairan minimal 2 liter air putih per hari!" + disclaimer
            }
            "kram" in lower || "sakit" in lower || "nyeri" in lower -> {
                "Kram perut ringan dapat diredakan dengan kompres hangat pada perut bagian bawah, peregangan yoga lembut (seperti child's pose), serta teh chamomile atau jahe hangat. PENTING: Jika rasa nyeri sangat hebat atau tidak tertahankan, segera konsultasikan langsung dengan dokter spesialis kandungan (Sp.OG)." + disclaimer
            }
            "ovulasi" in lower || "subur" in lower -> {
                "Fase ovulasi terjadi sekitar 14 hari sebelum hari pertama haid berikutnya. Di fase ini, lonjakan hormon LH memicu pelepasan sel telur, disertai peningkatan estrogen yang sering membuat energi fisik terasa optimal. Jendela masa subur berlangsung sekitar 5 hari sebelum dan saat hari ovulasi." + disclaimer
            }
            else -> {
                "Halo! Aku Lunara AI. Setiap fase siklus memiliki kebutuhan nutrisi dan ritme energi yang unik. Ceritakan apa yang sedang kamu rasakan hari ini—apakah tentang perubahan energi tubuh, suasana hati, atau tips menjaga kebugaran alami?" + disclaimer
            }
        }
    }
}
