package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ChatMessage
import com.example.ai.GeminiCycleService
import com.example.data.CycleCalculator
import com.example.data.model.CyclePrediction
import com.example.data.model.CycleRecord
import com.example.data.model.DailyLogEntry
import com.example.data.model.FlowLevel
import com.example.data.repository.CycleRepository
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class CycleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CycleRepository(application.applicationContext)
    private val geminiService = GeminiCycleService()

    val cycles: StateFlow<List<CycleRecord>> = repository.cycles
    val dailyLogs: StateFlow<Map<String, DailyLogEntry>> = repository.dailyLogs
    val currentUser = repository.currentUser
    val userName = repository.userName
    val isAiConsentGiven = repository.isAiConsentGiven
    val isCloudSyncEnabled = repository.isCloudSyncEnabled
    val isNotificationEnabled = repository.isNotificationEnabled
    val isBiometricEnabled = repository.isBiometricEnabled
    val isScreenDisguiseEnabled = repository.isScreenDisguiseEnabled
    val isDisguisedNotificationEnabled = repository.isDisguisedNotificationEnabled

    fun setUserName(name: String) = repository.setUserName(name)
    fun setAiConsentGiven(given: Boolean) = repository.setAiConsentGiven(given)

    private val calendarNow = Calendar.getInstance()
    private val todayYear = calendarNow.get(Calendar.YEAR)
    private val todayMonth = calendarNow.get(Calendar.MONTH) + 1
    private val todayDay = calendarNow.get(Calendar.DAY_OF_MONTH)
    val todayDateString: String = String.format("%04d-%02d-%02d", todayYear, todayMonth, todayDay)

    private val _selectedCalendarYear = MutableStateFlow(todayYear)
    val selectedCalendarYear: StateFlow<Int> = _selectedCalendarYear.asStateFlow()

    private val _selectedCalendarMonth = MutableStateFlow(todayMonth)
    val selectedCalendarMonth: StateFlow<Int> = _selectedCalendarMonth.asStateFlow()

    private val _selectedLogDate = MutableStateFlow(todayDateString)
    val selectedLogDate: StateFlow<String> = _selectedLogDate.asStateFlow()

    private val _isLogSheetVisible = MutableStateFlow(false)
    val isLogSheetVisible: StateFlow<Boolean> = _isLogSheetVisible.asStateFlow()

    private val _isExportDialogVisible = MutableStateFlow(false)
    val isExportDialogVisible: StateFlow<Boolean> = _isExportDialogVisible.asStateFlow()

    // Navigation indices: 0: Beranda, 1: Siklus Saya, 2: Kalender, 3: Wawasan, 4: Profil
    private val _currentNavIndex = MutableStateFlow(0)
    val currentNavIndex: StateFlow<Int> = _currentNavIndex.asStateFlow()

    val prediction: StateFlow<CyclePrediction> = cycles.combine(_selectedCalendarMonth) { cycleList, _ ->
        val now = Calendar.getInstance()
        CycleCalculator.calculatePrediction(
            cycles = cycleList,
            currentYear = now.get(Calendar.YEAR),
            currentMonth = now.get(Calendar.MONTH) + 1,
            currentDay = now.get(Calendar.DAY_OF_MONTH)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CycleCalculator.calculatePrediction(emptyList())
    )

    // Chat AI state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "model",
                text = "Halo! Aku Lunara AI, asisten pendamping ritme biologis dan kesehatan reproduksimu. Ada yang ingin kamu diskusikan hari ini seputar nutrisi, fluktuasi energi, atau kenyamanan tubuhmu? (Prediksi siklus bersifat estimasi edukatif dan bukan diagnosis medis)."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        NotificationHelper.createNotificationChannel(application.applicationContext)
    }

    fun setNavIndex(index: Int) {
        _currentNavIndex.value = index
    }

    fun openLogSheet(dateString: String = todayDateString) {
        _selectedLogDate.value = dateString
        _isLogSheetVisible.value = true
    }

    fun closeLogSheet() {
        _isLogSheetVisible.value = false
    }

    fun openExportDialog() {
        _isExportDialogVisible.value = true
    }

    fun closeExportDialog() {
        _isExportDialogVisible.value = false
    }

    fun previousMonth() {
        if (_selectedCalendarMonth.value == 1) {
            _selectedCalendarMonth.value = 12
            _selectedCalendarYear.value -= 1
        } else {
            _selectedCalendarMonth.value -= 1
        }
    }

    fun nextMonth() {
        if (_selectedCalendarMonth.value == 12) {
            _selectedCalendarMonth.value = 1
            _selectedCalendarYear.value += 1
        } else {
            _selectedCalendarMonth.value += 1
        }
    }

    fun saveDailyLog(log: DailyLogEntry) {
        repository.saveDailyLog(log)

        // Jika mencatat aliran haid (LIGHT, MEDIUM, HEAVY) dan belum ada siklus pada bulan ini, buat siklus baru
        if (log.flowLevel != FlowLevel.NONE && log.flowLevel != FlowLevel.SPOTTING) {
            val parts = log.dateString.split("-")
            if (parts.size == 3) {
                val y = parts[0].toIntOrNull() ?: todayYear
                val m = parts[1].toIntOrNull() ?: todayMonth
                val d = parts[2].toIntOrNull() ?: todayDay

                val existingCycleThisMonth = cycles.value.firstOrNull { it.startDateYear == y && it.startDateMonth == m }
                if (existingCycleThisMonth == null) {
                    val newCycle = CycleRecord(
                        id = "cycle_${y}_${m}_$d",
                        startDateYear = y,
                        startDateMonth = m,
                        startDateDay = d,
                        durationDays = 5,
                        cycleLengthDays = prediction.value.averageCycleLength,
                        notes = "Dicatat otomatis dari log aliran darah haid."
                    )
                    repository.saveCycleRecord(newCycle)
                }
            }
        }

        closeLogSheet()
    }

    fun saveCycle(cycle: CycleRecord) {
        repository.saveCycleRecord(cycle)
    }

    fun toggleCloudSync(enabled: Boolean) = repository.toggleCloudSync(enabled)

    fun toggleNotification(enabled: Boolean) {
        repository.toggleNotification(enabled)
        if (enabled) {
            NotificationHelper.sendTestNotification(
                context = getApplication<Application>().applicationContext,
                isDisguised = isDisguisedNotificationEnabled.value
            )
        }
    }

    fun triggerPeriodNotification() {
        NotificationHelper.sendPeriodReminderNotification(
            context = getApplication<Application>().applicationContext,
            daysRemaining = prediction.value.nextPeriodDaysRemaining,
            isDisguised = isDisguisedNotificationEnabled.value
        )
    }

    fun triggerFertileNotification() {
        NotificationHelper.sendFertileWindowReminder(
            context = getApplication<Application>().applicationContext,
            isDisguised = isDisguisedNotificationEnabled.value
        )
    }

    fun triggerPmsNotification() {
        NotificationHelper.sendPmsReminder(
            context = getApplication<Application>().applicationContext,
            isDisguised = isDisguisedNotificationEnabled.value
        )
    }

    fun toggleBiometric(enabled: Boolean) = repository.toggleBiometric(enabled)
    fun toggleScreenDisguise(enabled: Boolean) = repository.toggleScreenDisguise(enabled)
    fun toggleDisguisedNotification(enabled: Boolean) = repository.toggleDisguisedNotification(enabled)

    fun sendAiMessage(userText: String, useHighThinking: Boolean = false, useSearch: Boolean = true) {
        if (userText.isBlank() || _isAiLoading.value) return
        val userMsg = ChatMessage(sender = "user", text = userText)
        val currentHistory = _chatMessages.value + userMsg
        _chatMessages.value = currentHistory
        _isAiLoading.value = true

        viewModelScope.launch {
            try {
                val reply = geminiService.sendMessage(
                    history = currentHistory,
                    userMessage = userText,
                    useDeepThinking = useHighThinking,
                    useSearchGrounding = useSearch
                )
                _chatMessages.value = _chatMessages.value + reply
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun exportDataJson(timeRange: String): String {
        return repository.exportToJson(timeRange)
    }
}
