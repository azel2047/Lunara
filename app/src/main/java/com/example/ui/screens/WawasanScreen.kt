package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiCycleService
import com.example.ui.CycleViewModel
import com.example.ui.components.ClayCard
import com.example.ui.components.HormoneWaveChart
import com.example.ui.theme.LunaraBorderPink
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraLavenderSubtle
import com.example.ui.theme.LunaraPeachSubtle
import com.example.ui.theme.LunaraPhaseFertile
import com.example.ui.theme.LunaraPhasePMS
import com.example.ui.theme.LunaraPhasePeriod
import com.example.ui.theme.LunaraPinkSubtle
import com.example.ui.theme.LunaraPinkTint
import com.example.ui.theme.LunaraPinkWash
import com.example.ui.theme.LunaraPrimaryDark
import com.example.ui.theme.LunaraPrimaryStrong
import com.example.ui.theme.LunaraPrimaryText
import com.example.ui.theme.LunaraSecondary
import com.example.ui.theme.LunaraSurfaceLowest
import com.example.ui.theme.LunaraTertiary
import com.example.ui.theme.PlusJakartaSans

@Composable
fun WawasanScreen(
    viewModel: CycleViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val prediction by viewModel.prediction.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val isAiConsentGiven by viewModel.isAiConsentGiven.collectAsState()

    var showAiConsentDialog by remember { mutableStateOf(false) }
    var pendingMessage by remember { mutableStateOf<String?>(null) }
    var selectedWindow by remember { mutableStateOf("Bulan Ini") }
    var chatInput by remember { mutableStateOf("") }
    var useDeepThinking by remember { mutableStateOf(false) }
    var useSearchGrounding by remember { mutableStateOf(true) }

    // Hitung frekuensi gejala dari data nyata pengguna
    val symptomFrequency = remember(dailyLogs) {
        val countMap = mutableMapOf<String, Int>()
        dailyLogs.values.forEach { log ->
            log.symptoms.forEach { sym ->
                countMap[sym] = (countMap[sym] ?: 0) + 1
            }
        }
        countMap.toList().sortedByDescending { it.second }.take(3)
    }

    val symptomDisplayNames = mapOf(
        "cramps" to "Kram Perut",
        "headache" to "Sakit Kepala",
        "fatigue" to "Lelah / Lemas",
        "bloating" to "Kembung",
        "tender_breasts" to "Payudara Sensitif",
        "backache" to "Nyeri Punggung",
        "cervical_fluid" to "Cairan Bening",
        "clear_skin" to "Kulit Bersih",
        "nausea" to "Mual",
        "insomnia" to "Sulit Tidur"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LunaraCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subheader & Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LAPORAN PERSONAL",
                    fontFamily = PlusJakartaSans,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInkMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Wawasan & Tren",
                    fontFamily = PlusJakartaSans,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
            }

            IconButton(
                onClick = { viewModel.openExportDialog() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LunaraSurfaceLowest)
                    .shadow(4.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Bagikan Wawasan",
                    tint = LunaraPrimaryStrong,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Time Window Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(LunaraPinkWash)
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Bulan Ini", "3 Bulan", "6 Bulan").forEach { tab ->
                val selected = selectedWindow == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (selected) LunaraPrimaryStrong else Color.Transparent)
                        .shadow(if (selected) 4.dp else 0.dp, CircleShape)
                        .clickable { selectedWindow = tab }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) Color.White else LunaraInkMuted
                    )
                }
            }
        }

        // Friendly Status Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(LunaraPinkWash, LunaraPinkTint, LunaraPeachSubtle)
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(LunaraSurfaceLowest)
                            .shadow(2.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = LunaraPrimaryStrong,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (prediction.isRegular) "Siklus Teratur (Estimasi)" else "Siklus Bervariasi (Estimasi)",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraPrimaryStrong
                        )
                        Text(
                            text = "Rata-rata ${prediction.averageCycleLength} hari • Durasi haid ~${prediction.averagePeriodLength} hari",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(LunaraSurfaceLowest.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (prediction.isRegular) "Normal" else "Variatif",
                        fontFamily = PlusJakartaSans,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraPrimaryStrong
                    )
                }
            }
        }

        // Hormone Fluctuation Curve Card
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Pola Fluktuasi Hormon",
                            fontFamily = PlusJakartaSans,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                        Text(
                            text = "Ilustrasi umum dinamika hormon, bukan data pribadi",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LunaraLavenderSubtle)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Ilustrasi Edukatif",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraSecondary
                        )
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HormoneLegendItem(color = LunaraPhasePeriod, label = "Estrogen")
                    HormoneLegendItem(color = LunaraPhaseFertile, label = "Progesteron")
                    HormoneLegendItem(color = LunaraPhasePMS, label = "LH (Lonjakan)")
                }

                // Hormone Wave Chart with dynamic active day
                HormoneWaveChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    activeDay = prediction.currentCycleDay
                )

                // 4 Cycle Phases Breakdown Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val p = prediction.currentPhase
                    PhaseGridCell(
                        phase = "Haid",
                        days = "H 1-${prediction.averagePeriodLength}",
                        color = LunaraPrimaryStrong,
                        bg = LunaraPinkWash,
                        isActive = (p == com.example.data.model.CyclePhase.MENSTRUATION),
                        modifier = Modifier.weight(1f)
                    )
                    PhaseGridCell(
                        phase = "Folikuler",
                        days = "H ${prediction.averagePeriodLength + 1}-${prediction.fertileWindowStartDay - 1}",
                        color = LunaraInkMuted,
                        bg = LunaraPinkWash,
                        isActive = (p == com.example.data.model.CyclePhase.FOLLICULAR),
                        modifier = Modifier.weight(1f)
                    )
                    PhaseGridCell(
                        phase = "Ovulasi",
                        days = "H ${prediction.fertileWindowStartDay}-${prediction.fertileWindowEndDay}",
                        color = LunaraSecondary,
                        bg = LunaraLavenderSubtle,
                        isActive = (p == com.example.data.model.CyclePhase.OVULATION),
                        modifier = Modifier.weight(1f)
                    )
                    PhaseGridCell(
                        phase = "Luteal",
                        days = "H ${prediction.fertileWindowEndDay + 1}-${prediction.averageCycleLength}",
                        color = LunaraTertiary,
                        bg = LunaraPeachSubtle,
                        isActive = (p == com.example.data.model.CyclePhase.LUTEAL),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Analysis of Today's Biology
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(LunaraPinkSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LunaraPrimaryStrong,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ANALISIS BIOLOGIS HARI INI",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraPrimaryText,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${prediction.currentPhase.displayName} (Hari ke-${prediction.currentCycleDay})",
                            fontFamily = PlusJakartaSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                    }
                }

                Text(
                    text = prediction.currentPhase.description,
                    fontFamily = PlusJakartaSans,
                    fontSize = 13.sp,
                    color = LunaraInkMuted,
                    lineHeight = 19.sp
                )
            }
        }

        // Trends & Symptoms Correlation Card
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tren Gejala & Suasana Hati",
                            fontFamily = PlusJakartaSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                        Text(
                            text = "Berdasarkan ${dailyLogs.size} catatan mandiri yang tersimpan di perangkat",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = null,
                        tint = LunaraInkMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Frequent Symptom Chips (Computed from real data)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "PALING SERING TERCATAT",
                        fontFamily = PlusJakartaSans,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInkMuted,
                        letterSpacing = 0.8.sp
                    )

                    if (symptomFrequency.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            symptomFrequency.forEach { (symId, count) ->
                                val name = symptomDisplayNames[symId] ?: symId
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(LunaraPinkWash)
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "• $name ($count kali)",
                                        fontFamily = PlusJakartaSans,
                                        fontSize = 11.sp,
                                        color = LunaraPrimaryStrong
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Belum ada gejala tercatat. Catat gejala harianmu untuk melihat tren otomatis.",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted
                        )
                    }
                }
            }
        }

        // PDF Export Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(LunaraPinkSubtle.copy(alpha = 0.4f), LunaraSurfaceLowest, LunaraPeachSubtle.copy(alpha = 0.5f))
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LunaraSurfaceLowest)
                            .shadow(2.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalCafe,
                            contentDescription = null,
                            tint = LunaraPrimaryStrong,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Laporan Siklus Terstruktur",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                        Text(
                            text = "Ringkasan klinis untuk pendampingan dokter",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted
                        )
                    }
                }

                Button(
                    onClick = { viewModel.openExportDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_unduh_laporan_pdf"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LunaraSurfaceLowest,
                        contentColor = LunaraPrimaryStrong
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Unduh / Bagikan Salinan Data",
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Gemini AI Chat Assistant
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LunaraPrimaryStrong),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Konselor Lunara AI",
                                fontFamily = PlusJakartaSans,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInk
                            )
                            Text(
                                text = "Model: ${GeminiCycleService.DEFAULT_GEMINI_MODEL} (Edukasi Kesehatan)",
                                fontFamily = PlusJakartaSans,
                                fontSize = 10.sp,
                                color = LunaraPrimaryText
                            )
                        }
                    }
                }

                // Chat Messages Thread
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LunaraPinkWash.copy(alpha = 0.5f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chatMessages.forEach { msg ->
                        val isUser = msg.sender == "user"
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .background(if (isUser) LunaraPrimaryStrong else LunaraSurfaceLowest)
                                    .border(
                                        1.dp,
                                        if (isUser) Color.Transparent else LunaraBorderPink.copy(alpha = 0.5f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 12.sp,
                                    color = if (isUser) Color.White else LunaraInk,
                                    lineHeight = 16.sp
                                )
                            }
                            if (msg.searchSources.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Referensi: ${msg.searchSources.take(2).joinToString(", ")}",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 9.sp,
                                    color = LunaraInkMuted
                                )
                            }
                        }
                    }

                    if (isAiLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = LunaraPrimaryStrong,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Sedang menyiapkan wawasan edukatif...",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                color = LunaraInkMuted
                            )
                        }
                    }
                }

                // Chat Input Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        placeholder = {
                            Text(
                                text = "Tanyakan seputar nutrisi, kram, hormon...",
                                fontFamily = PlusJakartaSans,
                                fontSize = 12.sp,
                                color = LunaraInkMuted
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = PlusJakartaSans,
                            fontSize = 12.sp,
                            color = LunaraInk
                        ),
                        singleLine = true,
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LunaraPrimaryStrong,
                            unfocusedBorderColor = LunaraBorderPink,
                            focusedContainerColor = LunaraSurfaceLowest,
                            unfocusedContainerColor = LunaraSurfaceLowest
                        )
                    )

                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                if (!isAiConsentGiven) {
                                    pendingMessage = chatInput
                                    showAiConsentDialog = true
                                } else {
                                    val text = chatInput
                                    chatInput = ""
                                    viewModel.sendAiMessage(
                                        userText = text,
                                        useHighThinking = useDeepThinking,
                                        useSearch = useSearchGrounding
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LunaraPrimaryStrong)
                            .testTag("btn_send_ai_chat")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (showAiConsentDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    showAiConsentDialog = false
                    pendingMessage = null
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LunaraPrimaryStrong
                    )
                },
                title = {
                    Text(
                        text = "Persetujuan Privasi Lunara AI",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Sebelum menggunakan Konselor Lunara AI, mohon pahami pedoman privasi berikut:",
                            fontFamily = PlusJakartaSans,
                            fontSize = 12.sp,
                            color = LunaraInk
                        )
                        Text(
                            text = "• HANYA teks pertanyaan yang kamu ketik di kolom obrolan yang dikirim ke Google Gemini API.\n• Riwayat siklus, tanggal haid, catatan intim, gejala, dan data kesehatan pribadimu TIDAK PERNAH dikirim ke server AI.\n• Jawaban AI bersifat estimasi edukatif ritme reproduksi dan BUKAN diagnosis medis atau anjuran terapi pengganti dokter.",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted,
                            lineHeight = 16.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.setAiConsentGiven(true)
                            showAiConsentDialog = false
                            pendingMessage?.let { text ->
                                if (text.isNotBlank()) {
                                    chatInput = ""
                                    viewModel.sendAiMessage(
                                        userText = text,
                                        useHighThinking = useDeepThinking,
                                        useSearch = useSearchGrounding
                                    )
                                }
                            }
                            pendingMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaraPrimaryStrong)
                    ) {
                        Text("Saya Setuju & Lanjutkan", fontFamily = PlusJakartaSans)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        showAiConsentDialog = false
                        pendingMessage = null
                    }) {
                        Text("Batal", fontFamily = PlusJakartaSans)
                    }
                }
            )
        }

        // Disclaimer
        Text(
            text = "*Semua prediksi dan wawasan berlabel estimasi didasarkan pada perhitungan matematis ritme tubuh dan bukan merupakan diagnosis medis profesional. Konsultasikan keluhan serius ke dokter spesialis.",
            fontFamily = PlusJakartaSans,
            fontSize = 10.sp,
            color = LunaraInkMuted.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 68.dp)
        )
    }
}

@Composable
private fun HormoneLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontSize = 11.sp,
            color = LunaraInkMuted
        )
    }
}

@Composable
private fun PhaseGridCell(
    phase: String,
    days: String,
    color: Color,
    bg: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .then(
                if (isActive) Modifier.border(1.5.dp, color.copy(alpha = 0.7f), RoundedCornerShape(12.dp)) else Modifier
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = phase,
                fontFamily = PlusJakartaSans,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = days,
                fontFamily = PlusJakartaSans,
                fontSize = 9.sp,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}
