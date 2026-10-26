package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CycleCalculator
import com.example.data.model.CyclePhase
import com.example.data.model.CycleRecord
import com.example.data.model.DailyLogEntry
import com.example.data.model.FlowLevel
import com.example.data.model.MoodType
import com.example.data.repository.CycleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CycleFlowIntegrationTest {

    @Test
    fun testAlurCatatHaid_PrediksiBerubah_DanDataTetapSetelahRestart() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Inisialisasi Repository Sesi Pertama
        val repoSession1 = CycleRepository(context)

        // Verifikasi baseline awal
        val initialCycles = repoSession1.cycles.value
        assertTrue("Baseline awal harus tersedia", initialCycles.isNotEmpty())

        // 2. Catat Siklus Baru Pengguna: Tanggal 1 November 2024, Durasi 6 hari, Panjang siklus 30 hari
        val newCycle = CycleRecord(
            id = "cycle_nov_2024",
            startDateYear = 2024,
            startDateMonth = 11,
            startDateDay = 1,
            durationDays = 6,
            cycleLengthDays = 30,
            notes = "Uji alur pencatatan haid mandiri"
        )
        repoSession1.saveCycleRecord(newCycle)

        // Catat Log Harian dengan Aliran Darah dan Gejala
        val newLog = DailyLogEntry(
            dateString = "2024-11-01",
            dayOfMonth = 1,
            month = 11,
            year = 2024,
            flowLevel = FlowLevel.HEAVY,
            symptoms = setOf("cramps", "headache"),
            mood = MoodType.SAD,
            waterLiters = 2.0f,
            sleepHours = 8.0f
        )
        repoSession1.saveDailyLog(newLog)

        // 3. Verifikasi Prediksi Berubah Sesuai Rumus Medis Baru (Siklus 30 hari)
        // Cek pada hari ke-5 haid (5 November 2024)
        val prediction = CycleCalculator.calculatePrediction(repoSession1.cycles.value, 2024, 11, 5)

        assertEquals("Panjang siklus rata-rata harus terupdate jadi 30 hari", 30, prediction.averageCycleLength)
        assertEquals("Durasi haid harus terupdate jadi 6 hari", 6, prediction.averagePeriodLength)
        assertEquals("Fase saat ini harus Menstruasi", CyclePhase.MENSTRUATION, prediction.currentPhase)

        // Ovulasi = 14 hari sebelum siklus berikutnya (hari ke 30 - 14 + 1 = hari ke-17)
        assertEquals("Ovulasi pada siklus 30 hari harus hari ke-17", 17, prediction.ovulationDay)
        // Masa subur = hari 12 sampai 17
        assertEquals(12, prediction.fertileWindowStartDay)
        assertEquals(17, prediction.fertileWindowEndDay)
        // PMS = 5 hari terakhir (hari 26 sampai 30)
        assertEquals(26, prediction.pmsStartDay)
        assertEquals(30, prediction.pmsEndDay)

        // 4. Simulasi Restart Aplikasi (Matikan Sesi 1, Buat Instansiasi Repository Baru di Sesi 2)
        val repoSession2 = CycleRepository(context)

        // 5. Verifikasi Data Tetap Utuh dari Penyimpanan Lokal Setelah Restart
        val persistedCycles = repoSession2.cycles.value
        val persistedCycle = persistedCycles.firstOrNull { it.id == "cycle_nov_2024" }
        assertNotNull("Siklus harus tetap tersimpan setelah aplikasi ditutup/restart", persistedCycle)
        assertEquals(30, persistedCycle?.cycleLengthDays)
        assertEquals(6, persistedCycle?.durationDays)

        val persistedLogs = repoSession2.dailyLogs.value
        val persistedLog = persistedLogs["2024-11-01"]
        assertNotNull("Log harian harus tetap tersimpan setelah aplikasi ditutup/restart", persistedLog)
        assertEquals(FlowLevel.HEAVY, persistedLog?.flowLevel)
        assertTrue(persistedLog?.symptoms?.contains("cramps") == true)
        assertTrue(persistedLog?.symptoms?.contains("headache") == true)

        // Verifikasi kalkulasi pada sesi restart juga konsisten
        val predictionAfterRestart = CycleCalculator.calculatePrediction(repoSession2.cycles.value, 2024, 11, 5)
        assertEquals(30, predictionAfterRestart.averageCycleLength)
        assertEquals(17, predictionAfterRestart.ovulationDay)
    }
}
