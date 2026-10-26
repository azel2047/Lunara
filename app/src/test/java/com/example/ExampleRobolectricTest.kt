package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CycleCalculator
import com.example.data.model.CyclePhase
import com.example.data.model.CycleRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Lunara", appName)
    }

    @Test
    fun `test siklus 28 hari ovulasi hari 15 masa subur 10-15 dan PMS 24-28`() {
        val cycles = listOf(
            CycleRecord(
                id = "c1",
                startDateYear = 2024,
                startDateMonth = 10,
                startDateDay = 1,
                durationDays = 5,
                cycleLengthDays = 28
            )
        )
        // Cek pada hari ke-14 (14 Oktober 2024)
        val prediction = CycleCalculator.calculatePrediction(cycles, 2024, 10, 14)

        assertEquals(14, prediction.currentCycleDay)
        assertEquals(28, prediction.averageCycleLength)
        assertEquals(5, prediction.averagePeriodLength)

        // Verifikasi aturan medis yang ditentukan pengguna:
        // Ovulasi = 14 hari sebelum siklus berikutnya (hari ke-15 pada siklus 28 hari)
        assertEquals(15, prediction.ovulationDay)

        // Masa subur = 5 hari sebelum ovulasi sampai hari ovulasi (hari ke-10 sampai 15)
        assertEquals(10, prediction.fertileWindowStartDay)
        assertEquals(15, prediction.fertileWindowEndDay)

        // PMS = 5 hari terakhir sebelum haid berikutnya (hari ke-24 sampai 28)
        assertEquals(24, prediction.pmsStartDay)
        assertEquals(28, prediction.pmsEndDay)

        // Pada hari ke-14 berada di rentang masa subur (10..15)
        assertEquals(CyclePhase.OVULATION, prediction.currentPhase)
        assertFalse(prediction.isLate)
        assertEquals(0, prediction.daysLate)
        assertTrue(prediction.isRegular)
    }

    @Test
    fun `test siklus tidak teratur`() {
        val irregularCycles = listOf(
            CycleRecord(id = "c1", startDateYear = 2024, startDateMonth = 10, startDateDay = 1, durationDays = 5, cycleLengthDays = 24),
            CycleRecord(id = "c2", startDateYear = 2024, startDateMonth = 9, startDateDay = 1, durationDays = 5, cycleLengthDays = 34)
        )
        // Variasi 34 - 24 = 10 hari (> 3 hari)
        val prediction = CycleCalculator.calculatePrediction(irregularCycles, 2024, 10, 10)
        assertFalse(prediction.isRegular)
    }

    @Test
    fun `test data kosong menggunakan fallback 28 hari dan 5 hari haid`() {
        val prediction = CycleCalculator.calculatePrediction(emptyList(), 2024, 10, 1)

        assertEquals(28, prediction.averageCycleLength)
        assertEquals(5, prediction.averagePeriodLength)
        assertEquals(15, prediction.ovulationDay)
        assertEquals(10, prediction.fertileWindowStartDay)
        assertEquals(15, prediction.fertileWindowEndDay)
        assertEquals(24, prediction.pmsStartDay)
        assertEquals(28, prediction.pmsEndDay)
        assertFalse(prediction.hasUserData)
        assertFalse(prediction.isLate)
    }

    @Test
    fun `test haid terlambat`() {
        val cycles = listOf(
            CycleRecord(
                id = "c1",
                startDateYear = 2024,
                startDateMonth = 10,
                startDateDay = 1,
                durationDays = 5,
                cycleLengthDays = 28
            )
        )
        // Tanggal 5 November 2024 -> 35 hari setelah 1 Oktober (terlambat 7 hari dari siklus 28 hari)
        val prediction = CycleCalculator.calculatePrediction(cycles, 2024, 11, 5)

        assertEquals(36, prediction.currentCycleDay)
        assertTrue(prediction.isLate)
        assertEquals(8, prediction.daysLate)
        assertEquals(0, prediction.nextPeriodDaysRemaining)
    }
}
