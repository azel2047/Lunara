package com.example.util

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    private const val TAG = "CryptoHelper"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val GCM_TAG_LENGTH = 128
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12

    /**
     * Mengenkripsi teks dengan AES-256-GCM menggunakan kunci turunan dari kata sandi (PBKDF2).
     * Format output: [16-byte SALT] + [12-byte IV] + [CIPHERTEXT + 16-byte GCM TAG]
     */
    fun encryptAesGcm(plainText: String, password: String): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH).also { random.nextBytes(it) }
        val iv = ByteArray(IV_LENGTH).also { random.nextBytes(it) }

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val secretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // Gabungkan Salt + IV + CipherText
        val result = ByteArray(salt.size + iv.size + cipherText.size)
        System.arraycopy(salt, 0, result, 0, salt.size)
        System.arraycopy(iv, 0, result, salt.size, iv.size)
        System.arraycopy(cipherText, 0, result, salt.size + iv.size, cipherText.size)

        return result
    }

    /**
     * Menyimpan berkas ke direktori cache/exports dan membagikannya melalui Android Intent Chooser.
     */
    fun saveAndShareFile(
        context: Context,
        fileName: String,
        bytes: ByteArray,
        mimeType: String = "application/octet-stream"
    ): Boolean {
        return try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val file = File(exportDir, fileName)
            file.writeBytes(bytes)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Salinan Data Lunara: $fileName")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Bagikan Berkas Lunara").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membagikan berkas ekspor", e)
            false
        }
    }

    /**
     * Membuat laporan teks terstruktur yang ramah dokter (dapat disimpan sebagai .txt atau .pdf).
     */
    fun generateDoctorSummaryText(
        appName: String,
        exportedAt: String,
        avgCycleLength: Int,
        avgPeriodLength: Int,
        isRegular: Boolean,
        cyclesSummary: String,
        symptomsSummary: String
    ): String {
        return """
            ================================================================
                                LAPORAN KESEHATAN SIKLUS
                                   LUNARA CARE v2.4.0
            ================================================================
            Waktu Ekspor      : $exportedAt
            Kerahasiaan       : Data Medis Pribadi Terenkripsi di Perangkat

            RINGKASAN PARAMETER SIKLUS:
            - Rata-rata Panjang Siklus : $avgCycleLength hari
            - Rata-rata Durasi Haid    : $avgPeriodLength hari
            - Status Keteraturan       : ${if (isRegular) "Teratur (Variasi <= 3 hari)" else "Bervariasi"}

            RIWAYAT SIKLUS TERAKHIR:
            $cyclesSummary

            CATATAN GEJALA FISIK & MOOD TERDAFTAR:
            $symptomsSummary

            CATATAN PENTING:
            Laporan ini dihasilkan secara matematis berdasarkan pencatatan mandiri
            pengguna pada aplikasi Lunara Care dan dimaksudkan sebagai referensi
            pendukung konsultasi klinis, bukan merupakan diagnosis medis tunggal.
            ================================================================
        """.trimIndent()
    }
}
