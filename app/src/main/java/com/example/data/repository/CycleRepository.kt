package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.data.model.CycleRecord
import com.example.data.model.DailyLogEntry
import com.example.data.model.FlowLevel
import com.example.data.model.IntimacyType
import com.example.data.model.MoodType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class CycleRepository(private val context: Context) {

    private val prefs: SharedPreferences = createEncryptedSharedPreferences(context)

    companion object {
        private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
            return try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val encPrefs = EncryptedSharedPreferences.create(
                    context,
                    "lunara_encrypted_storage",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                // Migrasi data lama dari unencrypted SharedPreferences jika ada
                migrateFromPlainTextPrefs(context, encPrefs)
                encPrefs
            } catch (e: Exception) {
                Log.w("CycleRepository", "EncryptedSharedPreferences fallback ke local_storage", e)
                context.getSharedPreferences("lunara_local_storage", Context.MODE_PRIVATE)
            }
        }

        private fun migrateFromPlainTextPrefs(context: Context, encPrefs: SharedPreferences) {
            try {
                val oldPrefs = context.getSharedPreferences("lunara_local_storage", Context.MODE_PRIVATE)
                val allOld = oldPrefs.all
                if (allOld.isNotEmpty()) {
                    val editor = encPrefs.edit()
                    for ((k, v) in allOld) {
                        when (v) {
                            is String -> editor.putString(k, v)
                            is Boolean -> editor.putBoolean(k, v)
                            is Int -> editor.putInt(k, v)
                            is Long -> editor.putLong(k, v)
                            is Float -> editor.putFloat(k, v)
                        }
                    }
                    editor.apply()
                    oldPrefs.edit().clear().apply()
                    Log.i("CycleRepository", "Migrasi ${allOld.size} entri ke EncryptedSharedPreferences berhasil.")
                }
            } catch (e: Exception) {
                Log.e("CycleRepository", "Gagal memigrasi plaintext SharedPreferences", e)
            }
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("pref_user_name", "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isAiConsentGiven = MutableStateFlow(prefs.getBoolean("pref_ai_consent_given", false))
    val isAiConsentGiven: StateFlow<Boolean> = _isAiConsentGiven.asStateFlow()

    private val _cycles = MutableStateFlow<List<CycleRecord>>(emptyList())
    val cycles: StateFlow<List<CycleRecord>> = _cycles.asStateFlow()

    private val _dailyLogs = MutableStateFlow<Map<String, DailyLogEntry>>(emptyMap())
    val dailyLogs: StateFlow<Map<String, DailyLogEntry>> = _dailyLogs.asStateFlow()

    // Default: Data hanya di perangkat, Cloud Sync MATI secara default
    private val _isCloudSyncEnabled = MutableStateFlow(
        prefs.getBoolean("pref_cloud_sync_enabled", false)
    )
    val isCloudSyncEnabled: StateFlow<Boolean> = _isCloudSyncEnabled.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(
        prefs.getBoolean("pref_notifications_enabled", true)
    )
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(
        prefs.getBoolean("pref_biometric_enabled", false)
    )
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isScreenDisguiseEnabled = MutableStateFlow(
        prefs.getBoolean("pref_screen_disguise", false)
    )
    val isScreenDisguiseEnabled: StateFlow<Boolean> = _isScreenDisguiseEnabled.asStateFlow()

    private val _isDisguisedNotificationEnabled = MutableStateFlow(
        prefs.getBoolean("pref_disguised_notif", false)
    )
    val isDisguisedNotificationEnabled: StateFlow<Boolean> = _isDisguisedNotificationEnabled.asStateFlow()

    init {
        auth?.let { fbAuth ->
            _currentUser.value = fbAuth.currentUser
            fbAuth.addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
                if (_isCloudSyncEnabled.value) {
                    loadFromFirestoreIfAvailable()
                }
            }
        }

        loadFromLocalStorage()
    }

    private fun loadFromLocalStorage() {
        val savedCyclesJson = prefs.getString("local_cycles", null)
        val savedLogsJson = prefs.getString("local_daily_logs", null)

        if (savedCyclesJson != null && savedLogsJson != null) {
            try {
                val cycleList = mutableListOf<CycleRecord>()
                val cyclesArray = JSONArray(savedCyclesJson)
                for (i in 0 until cyclesArray.length()) {
                    val obj = cyclesArray.getJSONObject(i)
                    cycleList.add(
                        CycleRecord(
                            id = obj.getString("id"),
                            startDateYear = obj.getInt("startDateYear"),
                            startDateMonth = obj.getInt("startDateMonth"),
                            startDateDay = obj.getInt("startDateDay"),
                            durationDays = obj.getInt("durationDays"),
                            cycleLengthDays = obj.getInt("cycleLengthDays"),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
                _cycles.value = cycleList

                val logMap = mutableMapOf<String, DailyLogEntry>()
                val logsObj = JSONObject(savedLogsJson)
                val keys = logsObj.keys()
                while (keys.hasNext()) {
                    val dateKey = keys.next()
                    val obj = logsObj.getJSONObject(dateKey)

                    val symptomsSet = mutableSetOf<String>()
                    val symArray = obj.optJSONArray("symptoms")
                    if (symArray != null) {
                        for (j in 0 until symArray.length()) {
                            symptomsSet.add(symArray.getString(j))
                        }
                    }

                    val intimacySet = mutableSetOf<IntimacyType>()
                    val intmArray = obj.optJSONArray("intimacy")
                    if (intmArray != null) {
                        for (k in 0 until intmArray.length()) {
                            runCatching {
                                intimacySet.add(IntimacyType.valueOf(intmArray.getString(k)))
                            }
                        }
                    }

                    val flow = runCatching { FlowLevel.valueOf(obj.optString("flowLevel", "NONE")) }.getOrDefault(FlowLevel.NONE)
                    val mood = runCatching { MoodType.valueOf(obj.optString("mood", "HAPPY")) }.getOrDefault(MoodType.HAPPY)

                    logMap[dateKey] = DailyLogEntry(
                        dateString = obj.optString("dateString", dateKey),
                        dayOfMonth = obj.optInt("dayOfMonth", 1),
                        month = obj.optInt("month", 1),
                        year = obj.optInt("year", 2024),
                        flowLevel = flow,
                        symptoms = symptomsSet,
                        mood = mood,
                        intimacy = intimacySet,
                        privateNotes = obj.optString("privateNotes", ""),
                        waterLiters = obj.optDouble("waterLiters", 0.0).toFloat(),
                        sleepHours = obj.optDouble("sleepHours", 0.0).toFloat(),
                        workoutMinutes = obj.optInt("workoutMinutes", 0),
                        workoutType = obj.optString("workoutType", "")
                    )
                }
                _dailyLogs.value = logMap
                return
            } catch (e: Exception) {
                Log.w("CycleRepository", "Gagal memuat penyimpanan lokal, inisialisasi baru", e)
            }
        }

        seedInitialBaselineData()
    }

    private fun seedInitialBaselineData() {
        val now = Calendar.getInstance()
        val curYear = now.get(Calendar.YEAR)
        val curMonth = now.get(Calendar.MONTH) + 1

        val initialCycles = listOf(
            CycleRecord(
                id = "cycle_1",
                startDateYear = curYear,
                startDateMonth = curMonth,
                startDateDay = 1,
                durationDays = 5,
                cycleLengthDays = 28,
                notes = "Siklus baseline awal."
            )
        )
        _cycles.value = initialCycles

        val initialLogs = mutableMapOf<String, DailyLogEntry>()
        // Catat hari 1 haid
        val day1Key = String.format("%04d-%02d-01", curYear, curMonth)
        initialLogs[day1Key] = DailyLogEntry(
            dateString = day1Key,
            dayOfMonth = 1,
            month = curMonth,
            year = curYear,
            flowLevel = FlowLevel.MEDIUM,
            symptoms = setOf("cramps", "fatigue"),
            mood = MoodType.CALM,
            waterLiters = 2.0f,
            sleepHours = 7.5f,
            workoutMinutes = 15,
            workoutType = "Stretching"
        )

        _dailyLogs.value = initialLogs
        persistToLocalStorage()
    }

    private fun persistToLocalStorage() {
        try {
            val cyclesArray = JSONArray()
            _cycles.value.forEach { c ->
                val obj = JSONObject()
                obj.put("id", c.id)
                obj.put("startDateYear", c.startDateYear)
                obj.put("startDateMonth", c.startDateMonth)
                obj.put("startDateDay", c.startDateDay)
                obj.put("durationDays", c.durationDays)
                obj.put("cycleLengthDays", c.cycleLengthDays)
                obj.put("notes", c.notes)
                cyclesArray.put(obj)
            }

            val logsObj = JSONObject()
            _dailyLogs.value.forEach { (dateKey, entry) ->
                val obj = JSONObject()
                obj.put("dateString", entry.dateString)
                obj.put("dayOfMonth", entry.dayOfMonth)
                obj.put("month", entry.month)
                obj.put("year", entry.year)
                obj.put("flowLevel", entry.flowLevel.name)
                obj.put("symptoms", JSONArray(entry.symptoms))
                obj.put("mood", entry.mood.name)
                obj.put("intimacy", JSONArray(entry.intimacy.map { it.name }))
                obj.put("privateNotes", entry.privateNotes)
                obj.put("waterLiters", entry.waterLiters.toDouble())
                obj.put("sleepHours", entry.sleepHours.toDouble())
                obj.put("workoutMinutes", entry.workoutMinutes)
                obj.put("workoutType", entry.workoutType)
                logsObj.put(dateKey, obj)
            }

            prefs.edit()
                .putString("local_cycles", cyclesArray.toString())
                .putString("local_daily_logs", logsObj.toString())
                .apply()
        } catch (e: Exception) {
            Log.e("CycleRepository", "Gagal menyimpan ke penyimpanan lokal", e)
        }
    }

    fun saveDailyLog(log: DailyLogEntry) {
        val updated = _dailyLogs.value.toMutableMap()
        updated[log.dateString] = log
        _dailyLogs.value = updated
        persistToLocalStorage()

        // Sync ke Firestore HANYA jika Cloud Sync aktif dan pengguna login
        if (_isCloudSyncEnabled.value) {
            val user = auth?.currentUser
            val fs = firestore
            if (user != null && fs != null) {
                val data = hashMapOf(
                    "dateString" to log.dateString,
                    "flowLevel" to log.flowLevel.name,
                    "symptoms" to log.symptoms.toList(),
                    "mood" to log.mood.name,
                    "intimacy" to log.intimacy.map { it.name },
                    "privateNotes" to log.privateNotes,
                    "waterLiters" to log.waterLiters,
                    "sleepHours" to log.sleepHours,
                    "workoutMinutes" to log.workoutMinutes,
                    "workoutType" to log.workoutType
                )
                fs.collection("users")
                    .document(user.uid)
                    .collection("daily_logs")
                    .document(log.dateString)
                    .set(data)
                    .addOnFailureListener { e ->
                        Log.w("CycleRepository", "Gagal menyimpan log ke Firestore", e)
                    }
            }
        }
    }

    fun saveCycleRecord(cycle: CycleRecord) {
        val list = _cycles.value.toMutableList()
        val index = list.indexOfFirst { it.id == cycle.id }
        if (index >= 0) {
            list[index] = cycle
        } else {
            list.add(0, cycle)
        }
        _cycles.value = list
        persistToLocalStorage()

        if (_isCloudSyncEnabled.value) {
            val user = auth?.currentUser
            val fs = firestore
            if (user != null && fs != null) {
                val data = hashMapOf(
                    "id" to cycle.id,
                    "startDateYear" to cycle.startDateYear,
                    "startDateMonth" to cycle.startDateMonth,
                    "startDateDay" to cycle.startDateDay,
                    "durationDays" to cycle.durationDays,
                    "cycleLengthDays" to cycle.cycleLengthDays,
                    "notes" to cycle.notes
                )
                fs.collection("users")
                    .document(user.uid)
                    .collection("cycles")
                    .document(cycle.id)
                    .set(data)
                    .addOnFailureListener { e ->
                        Log.w("CycleRepository", "Gagal menyimpan siklus ke Firestore", e)
                    }
            }
        }
    }

    fun setUserName(name: String) {
        _userName.value = name
        prefs.edit().putString("pref_user_name", name).apply()
    }

    fun setAiConsentGiven(given: Boolean) {
        _isAiConsentGiven.value = given
        prefs.edit().putBoolean("pref_ai_consent_given", given).apply()
    }

    fun toggleCloudSync(enabled: Boolean) {
        _isCloudSyncEnabled.value = enabled
        prefs.edit().putBoolean("pref_cloud_sync_enabled", enabled).apply()
        if (enabled) {
            loadFromFirestoreIfAvailable()
        }
    }

    fun toggleNotification(enabled: Boolean) {
        _isNotificationEnabled.value = enabled
        prefs.edit().putBoolean("pref_notifications_enabled", enabled).apply()
    }

    fun toggleBiometric(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
        prefs.edit().putBoolean("pref_biometric_enabled", enabled).apply()
    }

    fun toggleScreenDisguise(enabled: Boolean) {
        _isScreenDisguiseEnabled.value = enabled
        prefs.edit().putBoolean("pref_screen_disguise", enabled).apply()
    }

    fun toggleDisguisedNotification(enabled: Boolean) {
        _isDisguisedNotificationEnabled.value = enabled
        prefs.edit().putBoolean("pref_disguised_notif", enabled).apply()
    }

    fun exportToJson(timeRange: String): String {
        val root = JSONObject()
        root.put("app", "Lunara Care")
        root.put("version", "2.4.0")
        root.put("storage", "Local Android Device Storage")
        root.put("exportedAt", System.currentTimeMillis())
        root.put("timeRange", timeRange)

        val cyclesArray = JSONArray()
        _cycles.value.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("startDate", "${c.startDateYear}-${c.startDateMonth}-${c.startDateDay}")
            obj.put("durationDays", c.durationDays)
            obj.put("cycleLengthDays", c.cycleLengthDays)
            obj.put("notes", c.notes)
            cyclesArray.put(obj)
        }
        root.put("cycles", cyclesArray)

        val logsArray = JSONArray()
        _dailyLogs.value.values.forEach { log ->
            val obj = JSONObject()
            obj.put("date", log.dateString)
            obj.put("flow", log.flowLevel.name)
            obj.put("symptoms", JSONArray(log.symptoms))
            obj.put("mood", log.mood.name)
            obj.put("intimacy", JSONArray(log.intimacy.map { it.name }))
            obj.put("privateNotes", log.privateNotes)
            obj.put("waterLiters", log.waterLiters.toDouble())
            obj.put("sleepHours", log.sleepHours.toDouble())
            obj.put("workoutMinutes", log.workoutMinutes)
            obj.put("workoutType", log.workoutType)
            logsArray.put(obj)
        }
        root.put("dailyLogs", logsArray)

        return root.toString(2)
    }

    private fun loadFromFirestoreIfAvailable() {
        val user = auth.currentUser ?: return
        firestore.collection("users")
            .document(user.uid)
            .collection("cycles")
            .get()
            .addOnSuccessListener { snapshots ->
                if (!snapshots.isEmpty) {
                    val loaded = snapshots.documents.mapNotNull { doc ->
                        try {
                            CycleRecord(
                                id = doc.getString("id") ?: doc.id,
                                startDateYear = doc.getLong("startDateYear")?.toInt() ?: 2024,
                                startDateMonth = doc.getLong("startDateMonth")?.toInt() ?: 10,
                                startDateDay = doc.getLong("startDateDay")?.toInt() ?: 1,
                                durationDays = doc.getLong("durationDays")?.toInt() ?: 5,
                                cycleLengthDays = doc.getLong("cycleLengthDays")?.toInt() ?: 28,
                                notes = doc.getString("notes") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (loaded.isNotEmpty()) {
                        _cycles.value = loaded
                        persistToLocalStorage()
                    }
                }
            }
    }
}
