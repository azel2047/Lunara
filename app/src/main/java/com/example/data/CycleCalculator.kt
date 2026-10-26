package com.example.data

import com.example.data.model.CyclePhase
import com.example.data.model.CyclePrediction
import com.example.data.model.CycleRecord
import java.util.Calendar

object CycleCalculator {

    private val MONTH_NAMES = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")

    /**
     * Menghitung prediksi siklus menstruasi berdasarkan rumus medis standar:
     * - Haid berikutnya = tanggal mulai haid terakhir + rata-rata panjang siklus (maks 6 siklus terakhir, default 28 hari & 5 hari haid)
     * - Hari awal siklus berikutnya = hari ke-(panjang siklus + 1)
     * - Ovulasi = 14 hari sebelum hari awal siklus berikutnya (pada siklus 28 hari: hari ke-15)
     * - Masa subur = 5 hari sebelum ovulasi sampai hari ovulasi (pada siklus 28 hari: hari ke-10 sampai 15)
     * - PMS = 5 hari terakhir sebelum siklus berikutnya (pada siklus 28 hari: hari ke-24 sampai 28)
     */
    fun calculatePrediction(
        cycles: List<CycleRecord>,
        currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
        currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
        currentDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    ): CyclePrediction {
        if (cycles.isEmpty()) {
            val avgCycle = 28
            val avgPeriod = 5
            val ovulationDay = (avgCycle + 1) - 14 // 15
            val fertileStart = (ovulationDay - 5).coerceAtLeast(1) // 10
            val fertileEnd = ovulationDay // 15
            val pmsStart = avgCycle - 4 // 24
            val pmsEnd = avgCycle // 28

            // Tanpa data riwayat, gunakan tanggal hari ini sebagai baseline estimasi
            val calToday = Calendar.getInstance().apply {
                set(currentYear, currentMonth - 1, currentDay, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val calNext = (calToday.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, avgCycle)
            }
            val nextFormatted = "${calNext.get(Calendar.DAY_OF_MONTH)} ${MONTH_NAMES.getOrElse(calNext.get(Calendar.MONTH)) { "" }}"

            return CyclePrediction(
                currentCycleDay = 1,
                currentPhase = CyclePhase.MENSTRUATION,
                nextPeriodDaysRemaining = avgCycle - 1,
                nextPeriodDateFormatted = nextFormatted,
                averageCycleLength = avgCycle,
                averagePeriodLength = avgPeriod,
                fertileWindowStartDay = fertileStart,
                fertileWindowEndDay = fertileEnd,
                ovulationDay = ovulationDay,
                pmsStartDay = pmsStart,
                pmsEndDay = pmsEnd,
                isRegular = true,
                isLate = false,
                daysLate = 0,
                hasUserData = false
            )
        }

        // Rata-rata dari maksimal 6 siklus terakhir
        val recentCycles = cycles.take(6)
        val avgCycleLength = recentCycles.map { it.cycleLengthDays }.average().toInt().coerceIn(21, 45)
        val avgPeriodLength = recentCycles.map { it.durationDays }.average().toInt().coerceIn(3, 10)

        // Cari siklus paling terkini
        val latestCycle = cycles.maxByOrNull {
            it.startDateYear * 10000 + it.startDateMonth * 100 + it.startDateDay
        } ?: cycles.first()

        val calCurrent = Calendar.getInstance().apply {
            set(currentYear, currentMonth - 1, currentDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val calStart = Calendar.getInstance().apply {
            set(latestCycle.startDateYear, latestCycle.startDateMonth - 1, latestCycle.startDateDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = calCurrent.timeInMillis - calStart.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        // Hari dalam siklus (1-indexed: hari ke-1 adalah startDate)
        val currentDayInCycle = diffDays + 1

        // Rumus Ovulasi: 14 hari sebelum hari awal siklus berikutnya
        val nextCycleFirstDay = avgCycleLength + 1
        val ovulationDay = nextCycleFirstDay - 14 // Siklus 28 -> hari ke-15
        val fertileStart = (ovulationDay - 5).coerceAtLeast(1) // Siklus 28 -> hari ke-10
        val fertileEnd = ovulationDay // Siklus 28 -> hari ke-15
        val pmsStart = avgCycleLength - 4 // Siklus 28 -> hari ke-24
        val pmsEnd = avgCycleLength // Siklus 28 -> hari ke-28

        val isLate = currentDayInCycle > avgCycleLength
        val daysLate = if (isLate) currentDayInCycle - avgCycleLength else 0

        val currentPhase = when {
            isLate -> CyclePhase.LUTEAL
            currentDayInCycle <= avgPeriodLength -> CyclePhase.MENSTRUATION
            currentDayInCycle in (avgPeriodLength + 1) until fertileStart -> CyclePhase.FOLLICULAR
            currentDayInCycle in fertileStart..fertileEnd -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }

        val daysRemaining = if (isLate) 0 else (avgCycleLength - currentDayInCycle + 1)

        val calNextPeriod = (calStart.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, avgCycleLength)
        }
        val nextPeriodFormatted = "${calNextPeriod.get(Calendar.DAY_OF_MONTH)} ${MONTH_NAMES.getOrElse(calNextPeriod.get(Calendar.MONTH)) { "" }}"

        val lengthVariations = recentCycles.map { it.cycleLengthDays }
        val maxVar = if (lengthVariations.isNotEmpty()) lengthVariations.maxOrNull()!! - lengthVariations.minOrNull()!! else 0
        val isRegular = maxVar <= 3

        return CyclePrediction(
            currentCycleDay = currentDayInCycle,
            currentPhase = currentPhase,
            nextPeriodDaysRemaining = daysRemaining,
            nextPeriodDateFormatted = nextPeriodFormatted,
            averageCycleLength = avgCycleLength,
            averagePeriodLength = avgPeriodLength,
            fertileWindowStartDay = fertileStart,
            fertileWindowEndDay = fertileEnd,
            ovulationDay = ovulationDay,
            pmsStartDay = pmsStart,
            pmsEndDay = pmsEnd,
            isRegular = isRegular,
            isLate = isLate,
            daysLate = daysLate,
            hasUserData = true
        )
    }
}
