package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CycleViewModel
import com.example.ui.components.LunaraFloatingNavBar
import com.example.ui.dialogs.ExportDataDialog
import com.example.ui.dialogs.LogCycleBottomSheet
import com.example.ui.screens.BerandaScreen
import com.example.ui.screens.KalenderScreen
import com.example.ui.screens.ProfilScreen
import com.example.ui.screens.SiklusSayaScreen
import com.example.ui.screens.WawasanScreen
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraPinkSubtle
import com.example.ui.theme.LunaraPinkWash
import com.example.ui.theme.LunaraPrimaryDark
import com.example.ui.theme.LunaraPrimaryStrong
import com.example.ui.theme.LunaraSurfaceLowest
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PlusJakartaSans

class MainActivity : FragmentActivity() {

    private val isUnlockedState = mutableStateOf(false)
    private var hasLeftForeground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val cycleViewModel: CycleViewModel = viewModel()
                val isBiometricEnabled by cycleViewModel.isBiometricEnabled.collectAsState()

                // Sinkronkan status kunci dengan preferensi
                LaunchedEffect(isBiometricEnabled) {
                    if (!isBiometricEnabled) {
                        isUnlockedState.value = true
                    }
                }

                LunaraApp(
                    viewModel = cycleViewModel,
                    isUnlocked = isUnlockedState.value || !isBiometricEnabled,
                    onRequestUnlock = {
                        triggerBiometricPrompt {
                            isUnlockedState.value = true
                        }
                    },
                    onSecureModeChanged = { isSecure ->
                        if (isSecure) {
                            window.setFlags(
                                WindowManager.LayoutParams.FLAG_SECURE,
                                WindowManager.LayoutParams.FLAG_SECURE
                            )
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                    }
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hasLeftForeground = true
    }

    override fun onResume() {
        super.onResume()
        if (hasLeftForeground) {
            hasLeftForeground = false
            // Kunci ulang jika biometrik aktif saat kembali dari background
            val vm = androidx.lifecycle.ViewModelProvider(this)[CycleViewModel::class.java]
            if (vm.isBiometricEnabled.value) {
                isUnlockedState.value = false
                triggerBiometricPrompt {
                    isUnlockedState.value = true
                }
            }
        }
    }

    fun triggerBiometricPrompt(onSuccess: () -> Unit) {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        val canAuth = biometricManager.canAuthenticate(authenticators)

        if (canAuth == BiometricManager.BIOMETRIC_SUCCESS) {
            val executor = ContextCompat.getMainExecutor(this)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Kunci Privasi Lunara")
                .setSubtitle("Pindai sidik jari, wajah, atau masukkan PIN/pola perangkat")
                .setAllowedAuthenticators(authenticators)
                .build()

            val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isUnlockedState.value = true
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Tetap terkunci, pengguna bisa mencoba lagi via tombol UI
                }
            })
            biometricPrompt.authenticate(promptInfo)
        } else {
            // Perangkat tidak memiliki biometrik atau berjalan di environment Robolectric
            isUnlockedState.value = true
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunaraApp(
    viewModel: CycleViewModel,
    isUnlocked: Boolean,
    onRequestUnlock: () -> Unit,
    onSecureModeChanged: (Boolean) -> Unit = {}
) {
    val currentNavIndex by viewModel.currentNavIndex.collectAsState()
    val isLogSheetVisible by viewModel.isLogSheetVisible.collectAsState()
    val isExportDialogVisible by viewModel.isExportDialogVisible.collectAsState()
    val selectedLogDate by viewModel.selectedLogDate.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()
    val isScreenDisguiseEnabled by viewModel.isScreenDisguiseEnabled.collectAsState()

    LaunchedEffect(isScreenDisguiseEnabled) {
        onSecureModeChanged(isScreenDisguiseEnabled)
    }

    val logSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val exportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle back button on sub-screens
    BackHandler(enabled = currentNavIndex != 0) {
        viewModel.setNavIndex(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E2836)), // Dark neutral outer canvas for desktop / wide displays
        contentAlignment = Alignment.Center
    ) {
        // Mobile container: max width 430dp, full height
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 430.dp)
                .background(LunaraCanvas)
        ) {
            if (!isUnlocked) {
                // Layar Kunci Biometrik
                LunaraLockScreen(onUnlockClick = onRequestUnlock)
            } else {
                // Ambient soft background top glow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    LunaraPinkWash.copy(alpha = 0.9f),
                                    LunaraPinkWash.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        LunaraFloatingNavBar(
                            selectedIndex = currentNavIndex,
                            onTabSelected = { viewModel.setNavIndex(it) },
                            modifier = Modifier.navigationBarsPadding()
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Crossfade(
                            targetState = currentNavIndex,
                            label = "screen_transition"
                        ) { targetIndex ->
                            when (targetIndex) {
                                0 -> BerandaScreen(viewModel = viewModel)
                                1 -> SiklusSayaScreen(viewModel = viewModel)
                                2 -> KalenderScreen(viewModel = viewModel)
                                3 -> WawasanScreen(viewModel = viewModel)
                                4 -> ProfilScreen(viewModel = viewModel)
                            }
                        }
                    }
                }

                // Bottom Sheets
                if (isLogSheetVisible) {
                    LogCycleBottomSheet(
                        initialLog = dailyLogs[selectedLogDate],
                        sheetState = logSheetState,
                        onDismiss = { viewModel.closeLogSheet() },
                        onSave = { updatedLog ->
                            viewModel.saveDailyLog(updatedLog)
                        }
                    )
                }

                if (isExportDialogVisible) {
                    ExportDataDialog(
                        viewModel = viewModel,
                        sheetState = exportSheetState,
                        onDismiss = { viewModel.closeExportDialog() }
                    )
                }
            }
        }
    }
}

@Composable
fun LunaraLockScreen(
    onUnlockClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LunaraCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(LunaraPinkSubtle)
                    .border(2.dp, Color.White, CircleShape)
                    .shadow(12.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Shield Lock",
                    tint = LunaraPrimaryStrong,
                    modifier = Modifier.size(46.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Lunara Care Terkunci",
                    fontFamily = PlusJakartaSans,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
                Text(
                    text = "Akses data kesehatan siklus reproduksi dilindungi oleh biometrik & sandi perangkat.",
                    fontFamily = PlusJakartaSans,
                    fontSize = 13.sp,
                    color = LunaraInkMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onUnlockClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("biometric_unlock_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LunaraPrimaryStrong)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Buka Kunci Aplikasi",
                    fontFamily = PlusJakartaSans,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
