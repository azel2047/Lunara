package com.example.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CycleViewModel
import com.example.ui.theme.LunaraBorderPink
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraLavenderSubtle
import com.example.ui.theme.LunaraPhasePeriod
import com.example.ui.theme.LunaraPinkSubtle
import com.example.ui.theme.LunaraPinkTint
import com.example.ui.theme.LunaraPinkWash
import com.example.ui.theme.LunaraPrimaryDark
import com.example.ui.theme.LunaraPrimaryStrong
import com.example.ui.theme.LunaraPrimaryText
import com.example.ui.theme.LunaraSecondary
import com.example.ui.theme.LunaraStatusSuccess
import com.example.ui.theme.LunaraSurfaceLowest
import com.example.ui.theme.PlusJakartaSans
import com.example.util.CryptoHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDataDialog(
    viewModel: CycleViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prediction by viewModel.prediction.collectAsState()
    val cycles by viewModel.cycles.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()

    var selectedFormat by remember { mutableStateOf("json") } // "json" or "pdf"
    var selectedRange by remember { mutableStateOf("Semua Riwayat") }
    var password by remember { mutableStateOf("LunaraSandi2025#") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var exportSuccess by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LunaraCanvas,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 48.dp, height = 5.dp)
                    .clip(CircleShape)
                    .background(LunaraInkMuted.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(LunaraPinkWash)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = LunaraInkMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Modal Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(10.dp, CircleShape, ambientColor = LunaraPrimaryStrong.copy(alpha = 0.25f))
                        .clip(CircleShape)
                        .background(LunaraPinkSubtle)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = LunaraPrimaryStrong,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Ekspor & Bagikan Salinan Data",
                    fontFamily = PlusJakartaSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )

                Text(
                    text = "Buat salinan data siklus dan gejala yang aman. Berkas terenkripsi dengan AES-256-GCM langsung di ponselmu.",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    color = LunaraInkMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            // Section 1: Format Options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pilihan Format Salinan",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )

                // Option JSON AES-256
                val isJson = selectedFormat == "json"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isJson) 6.dp else 1.dp, RoundedCornerShape(16.dp), ambientColor = LunaraPrimaryStrong.copy(alpha = 0.12f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isJson) LunaraPinkWash else LunaraSurfaceLowest)
                        .border(
                            if (isJson) 1.5.dp else 1.dp,
                            if (isJson) LunaraPrimaryStrong else LunaraBorderPink.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedFormat = "json" }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(LunaraLavenderSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DataObject,
                                    contentDescription = null,
                                    tint = LunaraSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Arsip Terenkripsi",
                                        fontFamily = PlusJakartaSans,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LunaraInk
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(LunaraLavenderSubtle)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "JSON AES-256",
                                            fontFamily = PlusJakartaSans,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LunaraSecondary
                                        )
                                    }
                                }
                                Text(
                                    text = "Dienkripsi dengan sandi pilihanmu menggunakan AES-256-GCM.",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 11.sp,
                                    color = LunaraInkMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isJson) LunaraPhasePeriod else LunaraPinkWash),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isJson) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Option Text / Report
                val isPdf = selectedFormat == "pdf"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isPdf) 6.dp else 1.dp, RoundedCornerShape(16.dp), ambientColor = LunaraPrimaryStrong.copy(alpha = 0.12f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isPdf) LunaraPinkWash else LunaraSurfaceLowest)
                        .border(
                            if (isPdf) 1.5.dp else 1.dp,
                            if (isPdf) LunaraPrimaryStrong else LunaraBorderPink.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedFormat = "pdf" }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(LunaraPinkSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = LunaraPrimaryStrong,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Ringkasan Klinis Dokter",
                                        fontFamily = PlusJakartaSans,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LunaraInk
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(LunaraPinkTint)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Teks Medis",
                                            fontFamily = PlusJakartaSans,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LunaraPrimaryText
                                        )
                                    }
                                }
                                Text(
                                    text = "Dokumen terstruktur yang siap dibagikan untuk konsultasi dokter spesialis.",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 11.sp,
                                    color = LunaraInkMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isPdf) LunaraPhasePeriod else LunaraPinkWash),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPdf) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Password (Hanya untuk format JSON)
            if (selectedFormat == "json") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LunaraPinkWash)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kata Sandi Enkripsi (AES-256)",
                                fontFamily = PlusJakartaSans,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInk
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(LunaraPinkTint)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Wajib",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LunaraPrimaryStrong
                                )
                            }
                        }

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = LunaraPrimaryStrong,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Tampilkan Sandi",
                                        tint = LunaraInkMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LunaraPrimaryStrong,
                                unfocusedBorderColor = LunaraBorderPink,
                                focusedContainerColor = LunaraSurfaceLowest,
                                unfocusedContainerColor = LunaraSurfaceLowest
                            )
                        )
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (!isExporting) {
                            isExporting = true
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                    if (selectedFormat == "json") {
                                        val rawJson = viewModel.exportDataJson(selectedRange)
                                        val pass = if (password.isBlank()) "LunaraDefaultPassword#" else password
                                        val encryptedBytes = CryptoHelper.encryptAesGcm(rawJson, pass)
                                        val fileName = "lunara_export_$timeStamp.json.enc"
                                        CryptoHelper.saveAndShareFile(
                                            context = context,
                                            fileName = fileName,
                                            bytes = encryptedBytes,
                                            mimeType = "application/octet-stream"
                                        )
                                    } else {
                                        val cyclesSummary = cycles.joinToString("\n") { c ->
                                            "- Mulai ${c.startDateDay}/${c.startDateMonth}/${c.startDateYear}: Durasi ${c.durationDays} hari, Siklus ${c.cycleLengthDays} hari. ${c.notes}"
                                        }.ifBlank { "- Belum ada siklus historis tercatat." }

                                        val symptomsSummary = dailyLogs.values.take(15).joinToString("\n") { log ->
                                            "- ${log.dateString}: Aliran=${log.flowLevel.displayName}, Gejala=[${log.symptoms.joinToString()}], Mood=${log.mood.displayName}. Catatan: ${log.privateNotes}"
                                        }.ifBlank { "- Belum ada catatan harian." }

                                        val summaryText = CryptoHelper.generateDoctorSummaryText(
                                            appName = "Lunara Care",
                                            exportedAt = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")).format(Date()),
                                            avgCycleLength = prediction.averageCycleLength,
                                            avgPeriodLength = prediction.averagePeriodLength,
                                            isRegular = prediction.isRegular,
                                            cyclesSummary = cyclesSummary,
                                            symptomsSummary = symptomsSummary
                                        )

                                        val fileName = "lunara_laporan_klinis_$timeStamp.txt"
                                        CryptoHelper.saveAndShareFile(
                                            context = context,
                                            fileName = fileName,
                                            bytes = summaryText.toByteArray(Charsets.UTF_8),
                                            mimeType = "text/plain"
                                        )
                                    }
                                }

                                isExporting = false
                                exportSuccess = true
                                Toast.makeText(context, "Berkas siap dibagikan / disimpan!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = LunaraPrimaryStrong.copy(alpha = 0.3f),
                            spotColor = LunaraPrimaryStrong.copy(alpha = 0.4f)
                        )
                        .testTag("btn_export_download"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LunaraPrimaryStrong,
                        contentColor = Color.White
                    )
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Menyiapkan Berkas...",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buat & Bagikan Berkas",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tutup",
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = LunaraInkMuted
                    )
                }
            }
        }
    }
}
