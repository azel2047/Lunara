package com.example.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsPaused
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.CycleViewModel
import com.example.ui.components.ClayCard
import com.example.ui.theme.LunaraBorderPink
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraLavenderSubtle
import com.example.ui.theme.LunaraPeachSubtle
import com.example.ui.theme.LunaraPhaseFertile
import com.example.ui.theme.LunaraPinkSubtle
import com.example.ui.theme.LunaraPinkTint
import com.example.ui.theme.LunaraPinkWash
import com.example.ui.theme.LunaraPrimaryDark
import com.example.ui.theme.LunaraPrimaryStrong
import com.example.ui.theme.LunaraPrimaryText
import com.example.ui.theme.LunaraSecondary
import com.example.ui.theme.LunaraStatusError
import com.example.ui.theme.LunaraStatusSuccess
import com.example.ui.theme.LunaraSurfaceLowest
import com.example.ui.theme.LunaraTertiary
import com.example.ui.theme.PlusJakartaSans

@Composable
fun ProfilScreen(
    viewModel: CycleViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    val isNotificationsEnabled by viewModel.isNotificationEnabled.collectAsState()
    val isCloudSync by viewModel.isCloudSyncEnabled.collectAsState()
    val isBiometric by viewModel.isBiometricEnabled.collectAsState()
    val isScreenDisguise by viewModel.isScreenDisguiseEnabled.collectAsState()
    val isDisguisedNotif by viewModel.isDisguisedNotificationEnabled.collectAsState()
    val prediction by viewModel.prediction.collectAsState()

    val biometricManager = remember { androidx.biometric.BiometricManager.from(context) }
    val canBiometric = remember {
        biometricManager.canAuthenticate(
            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }
    val isBiometricSupported = canBiometric == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.toggleNotification(isGranted)
        if (isGranted) {
            Toast.makeText(context, "Notifikasi pengingat siklus aktif", Toast.LENGTH_SHORT).show()
        }
    }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var editCycleDays by remember { mutableStateOf("${prediction.averageCycleLength}") }
    var editPeriodDays by remember { mutableStateOf("${prediction.averagePeriodLength}") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LunaraCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LunaraPinkSubtle)
                        .border(1.dp, Color.White.copy(alpha = 0.9f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Lunara Logo",
                        tint = LunaraPrimaryStrong,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "LUNARA CARE",
                        fontFamily = PlusJakartaSans,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInkMuted,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Profil & Pengaturan",
                        fontFamily = PlusJakartaSans,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraPrimaryStrong
                    )
                }
            }
        }

        // Hero Clay Card: Profil & Status Privasi
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val displayName = if (userName.isBlank()) "Pengguna Lunara" else userName
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(LunaraPrimaryStrong, LunaraPinkTint)
                                )
                            )
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable { showEditNameDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userName.isNotBlank()) userName.take(1).uppercase() else "L",
                            fontFamily = PlusJakartaSans,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displayName,
                                fontFamily = PlusJakartaSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInk
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(LunaraPinkSubtle)
                                    .clickable { showEditNameDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = LunaraPrimaryText,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Ubah Nama",
                                        fontFamily = PlusJakartaSans,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LunaraPrimaryText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Penyimpanan Terenkripsi AES-256 (Sandbox Perangkat)",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LunaraPrimaryDark
                        )
                        Text(
                            text = "Data tersimpan privat & terenkripsi lokal di ponselmu (Android Keystore)",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            color = LunaraInkMuted
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LunaraPinkWash)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = LunaraStatusSuccess,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Offline First • Tanpa Pelacak Iklan",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LunaraPrimaryText
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Ringkasan Parameter Siklus
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PENGATURAN SIKLUS PRIBADI",
                fontFamily = PlusJakartaSans,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LunaraInkMuted,
                letterSpacing = 0.8.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CycleParamCard(
                    title = "Panjang Siklus",
                    value = "${prediction.averageCycleLength} Hari",
                    subtitle = "Ketuk utk ubah",
                    subtitleColor = LunaraPrimaryText,
                    icon = Icons.Default.Sync,
                    iconBg = LunaraPinkSubtle,
                    iconTint = LunaraPrimaryStrong,
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f)
                )
                CycleParamCard(
                    title = "Durasi Haid",
                    value = "${prediction.averagePeriodLength} Hari",
                    subtitle = "Ketuk utk ubah",
                    subtitleColor = LunaraStatusSuccess,
                    icon = Icons.Default.WaterDrop,
                    iconBg = LunaraPeachSubtle,
                    iconTint = LunaraTertiary,
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f)
                )
                CycleParamCard(
                    title = "Keteraturan",
                    value = if (prediction.isRegular) "Teratur" else "Variatif",
                    subtitle = "Kalkulasi",
                    subtitleColor = LunaraSecondary,
                    icon = Icons.Default.Psychology,
                    iconBg = LunaraLavenderSubtle,
                    iconTint = LunaraSecondary,
                    onClick = {
                        Toast.makeText(context, "Dihitung dari riwayat siklus", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 3: Pengingat & Privasi
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = LunaraPrimaryStrong,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Pengingat & Perlindungan Privasi",
                    fontFamily = PlusJakartaSans,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
            }

            // Switch Item 1: Push Notifications
            PrivacyToggleCard(
                title = "Pengingat Siklus (Push Notifikasi)",
                subtitle = "Estimasi haid, masa subur & fase PMS",
                icon = Icons.Default.NotificationsActive,
                iconBg = LunaraPinkSubtle,
                iconTint = LunaraPrimaryStrong,
                isChecked = isNotificationsEnabled,
                onCheckedChange = { enable ->
                    if (enable) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.toggleNotification(true)
                            Toast.makeText(context, "Notifikasi pengingat siklus aktif", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        viewModel.toggleNotification(false)
                        Toast.makeText(context, "Notifikasi dinonaktifkan", Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "switch_push_notifications"
            )

            // Test Notification Chips when enabled
            if (isNotificationsEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LunaraPinkWash.copy(alpha = 0.6f))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Uji Coba Pengingat Sekarang:",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(LunaraPinkTint)
                                    .clickable {
                                        viewModel.triggerPeriodNotification()
                                        Toast.makeText(context, "Notifikasi estimasi haid dikirim", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🌸 Haid",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LunaraPrimaryDark
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(LunaraLavenderSubtle)
                                    .clickable {
                                        viewModel.triggerFertileNotification()
                                        Toast.makeText(context, "Notifikasi masa subur dikirim", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "💜 Subur",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LunaraSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(LunaraPeachSubtle)
                                    .clickable {
                                        viewModel.triggerPmsNotification()
                                        Toast.makeText(context, "Notifikasi fase PMS dikirim", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🌿 PMS",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LunaraTertiary
                                )
                            }
                        }
                    }
                }
            }

            // Switch Item: Kunci Biometrik & Sandi Perangkat
            if (isBiometricSupported) {
                PrivacyToggleCard(
                    title = "Kunci Biometrik & Sandi Perangkat",
                    subtitle = "Wajibkan sidik jari/wajah/PIN saat membuka aplikasi",
                    icon = Icons.Default.Fingerprint,
                    iconBg = LunaraPinkSubtle,
                    iconTint = LunaraPrimaryStrong,
                    isChecked = isBiometric,
                    onCheckedChange = { enable ->
                        viewModel.toggleBiometric(enable)
                        val msg = if (enable) "Kunci biometrik diaktifkan" else "Kunci biometrik dinonaktifkan"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    testTag = "switch_biometric_lock"
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LunaraSurfaceLowest)
                        .border(1.dp, LunaraBorderPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LunaraPinkWash),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = LunaraInkMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Kunci Biometrik / PIN",
                                fontFamily = PlusJakartaSans,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInkMuted
                            )
                            Text(
                                text = "Sensor biometrik tidak tersedia atau belum didaftarkan di perangkat ini.",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                color = LunaraInkMuted
                            )
                        }
                    }
                }
            }

            // Switch Item 2: Mode Samaran Layar (FLAG_SECURE aktif)
            PrivacyToggleCard(
                title = "Mode Samaran Layar (Anti Screenshot)",
                subtitle = "Cegah screenshot & buramkan di App Switcher",
                icon = Icons.Default.BlurOn,
                iconBg = LunaraLavenderSubtle,
                iconTint = LunaraSecondary,
                isChecked = isScreenDisguise,
                onCheckedChange = {
                    viewModel.toggleScreenDisguise(it)
                    val msg = if (it) "Mode samaran layar aktif (screenshot diblokir)" else "Mode samaran dinonaktifkan"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                testTag = "switch_screen_disguise"
            )

            // Switch Item 3: Notifikasi Rahasia
            PrivacyToggleCard(
                title = "Notifikasi Samaran (Discreet)",
                subtitle = "Teks pengingat netral tanpa kata 'haid'",
                icon = Icons.Default.NotificationsPaused,
                iconBg = LunaraPeachSubtle,
                iconTint = LunaraTertiary,
                isChecked = isDisguisedNotif,
                onCheckedChange = { viewModel.toggleDisguisedNotification(it) },
                testTag = "switch_disguised_notif"
            )

            // Switch Item 4: Cloud Sync (Default OFF)
            PrivacyToggleCard(
                title = "Sinkronisasi Cadangan Cloud",
                subtitle = if (isCloudSync) "Aktif (Tersinkron ke Firebase Cloud)" else "Nonaktif (Data HANYA di ponselmu)",
                icon = Icons.Default.CloudSync,
                iconBg = LunaraPinkWash,
                iconTint = if (isCloudSync) LunaraPrimaryStrong else LunaraInkMuted,
                isChecked = isCloudSync,
                onCheckedChange = { enable ->
                    viewModel.toggleCloudSync(enable)
                    val msg = if (enable) "Sinkronisasi Cloud diaktifkan" else "Cloud sync dinonaktifkan. Data tetap aman di ponsel."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                testTag = "switch_cloud_sync"
            )
        }

        // Section 4: Kontrol Kepemilikan Data
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "KONTROL DATA PRIBADI",
                fontFamily = PlusJakartaSans,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LunaraInkMuted,
                letterSpacing = 0.8.sp
            )

            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DataActionRow(
                        title = "Unduh / Bagikan Salinan Data",
                        subtitle = "Format JSON Terenkripsi AES-GCM atau Ringkasan Medis",
                        icon = Icons.Default.DownloadForOffline,
                        iconBg = LunaraPinkTint,
                        iconTint = LunaraPrimaryStrong,
                        onClick = { viewModel.openExportDialog() },
                        testTag = "action_export_data"
                    )
                    DataActionRow(
                        title = "Hapus Data Siklus & Mulai Baru",
                        subtitle = "Reset seluruh data siklus lokal pada perangkat",
                        icon = Icons.Default.DeleteSweep,
                        iconBg = LunaraPinkTint,
                        iconTint = LunaraStatusError,
                        isDestructive = true,
                        onClick = { showDeleteConfirmDialog = true },
                        testTag = "action_delete_history"
                    )
                }
            }
        }

        // App Version
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Lunara Care v1.0 • Pelacak Siklus & Kesejahteraan Wanita",
                fontFamily = PlusJakartaSans,
                fontSize = 11.sp,
                color = LunaraInkMuted
            )
        }

        Spacer(modifier = Modifier.height(68.dp))
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(
                    text = "Ubah Nama Pengguna",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Nama ini hanya disimpan di memori lokal ponselmu untuk sapaan di aplikasi:",
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = LunaraInkMuted
                    )
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nama Panggilan", fontFamily = PlusJakartaSans) },
                        placeholder = { Text("Contoh: Maya", fontFamily = PlusJakartaSans) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setUserName(tempName.trim())
                        showEditNameDialog = false
                        Toast.makeText(context, "Nama berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LunaraPrimaryStrong)
                ) {
                    Text("Simpan", fontFamily = PlusJakartaSans)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Batal", fontFamily = PlusJakartaSans)
                }
            }
        )
    }

    // Edit Cycle Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Ubah Parameter Siklus",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Sesuaikan rata-rata panjang siklus dan durasi haidmu:",
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = LunaraInkMuted
                    )
                    OutlinedTextField(
                        value = editCycleDays,
                        onValueChange = { editCycleDays = it },
                        label = { Text("Panjang Siklus (Hari)", fontFamily = PlusJakartaSans) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPeriodDays,
                        onValueChange = { editPeriodDays = it },
                        label = { Text("Durasi Haid (Hari)", fontFamily = PlusJakartaSans) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cDays = (editCycleDays.toIntOrNull() ?: 28).coerceIn(21, 45)
                        val pDays = (editPeriodDays.toIntOrNull() ?: 5).coerceIn(3, 10)
                        val now = java.util.Calendar.getInstance()
                        viewModel.saveCycle(
                            com.example.data.model.CycleRecord(
                                id = "cycle_user_custom",
                                startDateYear = now.get(java.util.Calendar.YEAR),
                                startDateMonth = now.get(java.util.Calendar.MONTH) + 1,
                                startDateDay = 1,
                                durationDays = pDays,
                                cycleLengthDays = cDays
                            )
                        )
                        showEditDialog = false
                        Toast.makeText(context, "Parameter siklus berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LunaraPrimaryStrong)
                ) {
                    Text("Simpan", fontFamily = PlusJakartaSans)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal", fontFamily = PlusJakartaSans)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Reset Data Siklus?",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = LunaraStatusError
                )
            },
            text = {
                Text(
                    text = "Tindakan ini akan mengembalikan data siklus ke estimasi standar awal. Pastikan kamu sudah mengunduh salinan data jika diperlukan.",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    color = LunaraInkMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val now = java.util.Calendar.getInstance()
                        viewModel.saveCycle(
                            com.example.data.model.CycleRecord(
                                id = "cycle_reset",
                                startDateYear = now.get(java.util.Calendar.YEAR),
                                startDateMonth = now.get(java.util.Calendar.MONTH) + 1,
                                startDateDay = now.get(java.util.Calendar.DAY_OF_MONTH),
                                durationDays = 5,
                                cycleLengthDays = 28,
                                notes = "Reset mandiri."
                            )
                        )
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "Data siklus di-reset ke baseline", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LunaraStatusError)
                ) {
                    Text("Reset", fontFamily = PlusJakartaSans)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal", fontFamily = PlusJakartaSans)
                }
            }
        )
    }
}

@Composable
private fun CycleParamCard(
    title: String,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = LunaraPrimaryStrong.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(16.dp))
            .background(LunaraSurfaceLowest)
            .border(1.dp, LunaraBorderPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontFamily = PlusJakartaSans,
                fontSize = 10.sp,
                color = LunaraInkMuted
            )
            Text(
                text = value,
                fontFamily = PlusJakartaSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LunaraInk
            )
            Text(
                text = subtitle,
                fontFamily = PlusJakartaSans,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = subtitleColor
            )
        }
    }
}

@Composable
private fun PrivacyToggleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = LunaraPrimaryStrong.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(16.dp))
            .background(LunaraSurfaceLowest)
            .border(1.dp, LunaraBorderPink.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInk
                    )
                    Text(
                        text = subtitle,
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        color = LunaraInkMuted
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = LunaraPrimaryStrong,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = LunaraInkMuted.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun DataActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontFamily = PlusJakartaSans,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) LunaraStatusError else LunaraInk
                )
                Text(
                    text = subtitle,
                    fontFamily = PlusJakartaSans,
                    fontSize = 11.sp,
                    color = LunaraInkMuted
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (isDestructive) LunaraStatusError else LunaraInkMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
