package com.example.data.model

enum class FlowLevel(val displayName: String, val shortName: String) {
    NONE("Tidak Ada", "Tidak"),
    SPOTTING("Bercak / Flek", "Bercak"),
    LIGHT("Aliran Ringan", "Ringan"),
    MEDIUM("Aliran Sedang", "Sedang"),
    HEAVY("Aliran Deras", "Deras")
}

enum class MoodType(val displayName: String, val emoji: String) {
    HAPPY("Bahagia", "✨"),
    CALM("Tenang", "🌸"),
    SENSITIVE("Sensitif", "🥺"),
    ANXIOUS("Cemas", "💭"),
    TIRED("Lelah", "🌧️"),
    SAD("Sedih / Murung", "😢"),
    IRRITABLE("Mudah Marah", "😤"),
    ENERGETIC("Bersemangat", "⚡")
}

enum class IntimacyType(val displayName: String, val emoji: String) {
    HIGH("Gairah Tinggi", "🔥"),
    MEDIUM("Sedang", "🌱"),
    LOW("Rendah", "💤"),
    PROTECTED("Terlindungi", "🛡️")
}

enum class CyclePhase(val displayName: String, val description: String) {
    MENSTRUATION("Fase Menstruasi", "Pelepasan dinding rahim, fokus pada istirahat hangat dan nutrisi zat besi."),
    FOLLICULAR("Fase Folikuler", "Estrogen mulai meningkat, energi bertambah dan kreativitas memuncak."),
    OVULATION("Fase Ovulasi", "Masa subur puncak, pelepasan sel telur dengan energi fisik tertinggi."),
    LUTEAL("Fase Luteal", "Progesteron dominan, waktu menenangkan diri dan antisipasi gejala PMS.")
}

data class PhysicalSymptom(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val category: String = "Umum"
)

data class CycleRecord(
    val id: String = "",
    val startDateYear: Int = 2024,
    val startDateMonth: Int = 10,
    val startDateDay: Int = 1,
    val durationDays: Int = 5,
    val cycleLengthDays: Int = 28,
    val notes: String = ""
)

data class DailyLogEntry(
    val dateString: String = "2024-10-14",
    val dayOfMonth: Int = 14,
    val month: Int = 10,
    val year: Int = 2024,
    val flowLevel: FlowLevel = FlowLevel.NONE,
    val symptoms: Set<String> = setOf("cramps", "cervical_fluid", "tender_breasts"),
    val mood: MoodType = MoodType.HAPPY,
    val intimacy: Set<IntimacyType> = setOf(IntimacyType.HIGH, IntimacyType.PROTECTED),
    val privateNotes: String = "",
    val waterLiters: Float = 1.5f,
    val sleepHours: Float = 7.0f,
    val workoutMinutes: Int = 20,
    val workoutType: String = "Yoga"
)

data class CyclePrediction(
    val currentCycleDay: Int = 1,
    val currentPhase: CyclePhase = CyclePhase.MENSTRUATION,
    val nextPeriodDaysRemaining: Int = 27,
    val nextPeriodDateFormatted: String = "",
    val averageCycleLength: Int = 28,
    val averagePeriodLength: Int = 5,
    val fertileWindowStartDay: Int = 10,
    val fertileWindowEndDay: Int = 15,
    val ovulationDay: Int = 15,
    val pmsStartDay: Int = 24,
    val pmsEndDay: Int = 28,
    val isRegular: Boolean = true,
    val isLate: Boolean = false,
    val daysLate: Int = 0,
    val hasUserData: Boolean = true
)

