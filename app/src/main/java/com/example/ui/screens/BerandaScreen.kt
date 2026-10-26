package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.CycleViewModel
import com.example.ui.components.ClayCard
import com.example.ui.components.Glossy3DDroplet
import com.example.ui.theme.LunaraBorderPink
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraLavenderSubtle
import com.example.ui.theme.LunaraPeachSubtle
import com.example.ui.theme.LunaraPhaseFertile
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
import com.example.ui.theme.LunaraTertiary
import com.example.ui.theme.PlusJakartaSans

@Composable
fun BerandaScreen(
    viewModel: CycleViewModel,
    modifier: Modifier = Modifier
) {
    val prediction by viewModel.prediction.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val todayDate = viewModel.todayDateString
    val todayLog = dailyLogs[todayDate]
    val scrollState = rememberScrollState()

    var showPrivacyNotice by remember { mutableStateOf(false) }

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
                        text = "Beranda",
                        fontFamily = PlusJakartaSans,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraPrimaryStrong
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { showPrivacyNotice = !showPrivacyNotice },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LunaraSurfaceLowest.copy(alpha = 0.8f))
                        .testTag("privacy_quick_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privasi Cepat",
                        tint = if (showPrivacyNotice) LunaraPrimaryStrong else LunaraInkMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { viewModel.setNavIndex(4) }
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCYrAMMkESjyV8wbwIuSdVFm-AsFr3z0Oyq0qNggT66HdeCR5NjZypWrYG7BDoHImi8j_HWu7GA1ns_fqHYaXmCA5rNG-mmj4pUksRfiMHK-fwFWlgbFlspx5U0hKk0IspPh4mUbplA9NPLtRoye2koX5jCxsZhCCwPwVwCCb2ANQMe5wxToKjDNFIC4iUjolRmpP660hK-dexhnthYp7-tHLrudqR921hitqY6_14okjdrmZXBC545",
                        contentDescription = "Avatar Sasa",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Privacy reassurance badge notice
        AnimatedVisibility(visible = showPrivacyNotice) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LunaraPinkTint)
                    .padding(12.dp)
            ) {
                Text(
                    text = "🔒 Privasi Terlindungi: Semua data siklus dienkripsi dan hanya dapat diakses olehmu. Lunara tidak pernah membagikan datamu.",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    color = LunaraPrimaryDark
                )
            }
        }

        // Greeting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayName = if (userName.isBlank()) "Sahabat Lunara" else userName
            Column {
                Text(
                    text = "Halo, $displayName ✨",
                    fontFamily = PlusJakartaSans,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
                Text(
                    text = "Bagaimana perasaanmu hari ini?",
                    fontFamily = PlusJakartaSans,
                    fontSize = 13.sp,
                    color = LunaraInkMuted
                )
            }

            Box {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(LunaraPrimaryStrong, LunaraPinkTint)
                            )
                        )
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (userName.isNotBlank()) userName.take(1).uppercase() else "L",
                        fontFamily = PlusJakartaSans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(LunaraStatusSuccess)
                        .border(1.5.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        // Hero Cycle Card
        ClayCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Hari ke-",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            color = LunaraInkMuted
                        )
                        Text(
                            text = "${prediction.currentCycleDay}",
                            fontFamily = PlusJakartaSans,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk,
                            lineHeight = 48.sp
                        )
                    }

                    // Phase Badge
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LunaraPinkSubtle)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(LunaraPrimaryStrong)
                        )
                        Text(
                            text = prediction.currentPhase.displayName,
                            fontFamily = PlusJakartaSans,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LunaraPrimaryText
                        )
                    }

                    // Cycle countdown
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = LunaraPrimaryStrong,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (prediction.isLate) {
                                "Haid terlambat ${prediction.daysLate} hari (estimasi)"
                            } else {
                                "Haid berikutnya dalam ${prediction.nextPeriodDaysRemaining} hari (estimasi)"
                            },
                            fontFamily = PlusJakartaSans,
                            fontSize = 12.sp,
                            color = LunaraInkMuted
                        )
                    }
                }

                // 3D Inflated Glossy Droplet
                Glossy3DDroplet(modifier = Modifier.size(92.dp))
            }
        }

        // Pagination Dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 20.dp, height = 6.dp)
                    .clip(CircleShape)
                    .background(LunaraPhasePeriod)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(LunaraPinkSubtle)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(LunaraPinkSubtle)
            )
        }

        // Fertile Window Banner
        ClayCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.setNavIndex(2) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LunaraLavenderSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarViewWeek,
                            contentDescription = null,
                            tint = LunaraPhaseFertile,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "PELUANG KEHAMILAN",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInkMuted,
                            letterSpacing = 1.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Masa Subur",
                                fontFamily = PlusJakartaSans,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LunaraInk
                            )
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(LunaraPhaseFertile)
                            )
                            val fertileText = when {
                                prediction.currentCycleDay in prediction.fertileWindowStartDay..prediction.fertileWindowEndDay -> "Sedang masa subur"
                                prediction.currentCycleDay < prediction.fertileWindowStartDay -> "Mulai ${prediction.fertileWindowStartDay - prediction.currentCycleDay} hari lagi"
                                else -> "Selesai (Fase Luteal)"
                            }
                            Text(
                                text = fertileText,
                                fontFamily = PlusJakartaSans,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = LunaraPhaseFertile
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(LunaraLavenderSubtle.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Buka kalender",
                        tint = LunaraSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Quick Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                label = "Catat Haid",
                icon = Icons.Default.WaterDrop,
                onClick = { viewModel.openLogSheet(todayDate) },
                testTag = "action_catat_haid"
            )
            QuickActionButton(
                label = "Gejala",
                icon = Icons.Default.AutoAwesome,
                onClick = { viewModel.openLogSheet(todayDate) },
                testTag = "action_gejala"
            )
            QuickActionButton(
                label = "Mood",
                icon = Icons.Default.SentimentSatisfied,
                onClick = { viewModel.openLogSheet(todayDate) },
                testTag = "action_mood"
            )
        }

        // Today's Wellness Tiles
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hari Ini",
                    fontFamily = PlusJakartaSans,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
                Text(
                    text = "Lihat semua",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LunaraPrimaryText,
                    modifier = Modifier.clickable { viewModel.setNavIndex(1) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val waterText = if (todayLog != null && todayLog.waterLiters > 0f) "${todayLog.waterLiters} L" else "Belum dicatat"
                val waterTag = if (todayLog != null && todayLog.waterLiters >= 2.0f) "Optimal" else "Target 2L"
                WellnessTile(
                    title = "Air Minum",
                    value = waterText,
                    tag = waterTag,
                    tagColor = LunaraPrimaryText,
                    tagBg = LunaraPinkSubtle.copy(alpha = 0.6f),
                    icon = Icons.Default.LocalDrink,
                    iconBg = LunaraPinkWash,
                    iconTint = LunaraPrimaryStrong,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.openLogSheet(todayDate) }
                )

                val sleepText = if (todayLog != null && todayLog.sleepHours > 0f) "${todayLog.sleepHours.toInt()} jam" else "Belum dicatat"
                val sleepTag = if (todayLog != null && todayLog.sleepHours >= 7f) "Cukup" else "Target 7-8j"
                WellnessTile(
                    title = "Tidur",
                    value = sleepText,
                    tag = sleepTag,
                    tagColor = LunaraSecondary,
                    tagBg = LunaraLavenderSubtle,
                    icon = Icons.Default.Bedtime,
                    iconBg = LunaraLavenderSubtle,
                    iconTint = LunaraSecondary,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.openLogSheet(todayDate) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val workoutText = if (todayLog != null && todayLog.workoutMinutes > 0) "${todayLog.workoutMinutes} mnt" else "Belum dicatat"
                val workoutTag = if (todayLog != null && todayLog.workoutType.isNotBlank()) todayLog.workoutType else "+ Catat"
                WellnessTile(
                    title = "Olahraga",
                    value = workoutText,
                    tag = workoutTag,
                    tagColor = LunaraTertiary,
                    tagBg = LunaraPeachSubtle,
                    icon = Icons.Default.FitnessCenter,
                    iconBg = LunaraPeachSubtle,
                    iconTint = LunaraTertiary,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.openLogSheet(todayDate) }
                )
                WellnessTile(
                    title = "Catatan",
                    value = if (todayLog?.privateNotes.isNullOrBlank()) "Belum diisi" else "Tercatat",
                    tag = "+",
                    tagColor = LunaraPrimaryStrong,
                    tagBg = LunaraPinkSubtle.copy(alpha = 0.5f),
                    icon = Icons.Default.EditNote,
                    iconBg = LunaraPinkWash,
                    iconTint = LunaraPrimaryStrong,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.openLogSheet(todayDate) }
                )
            }
        }

        // Affirmation & Daily Care Tip
        val phaseTip = when (prediction.currentPhase) {
            com.example.data.model.CyclePhase.MENSTRUATION -> "Fokus pada istirahat hangat, nutrisi kaya zat besi (bayam, kacang-kacangan), dan dengarkan sinyal tubuhmu."
            com.example.data.model.CyclePhase.FOLLICULAR -> "Estrogen mulai meningkat, waktu terbaik untuk mengeksplorasi ide kreatif dan aktivitas baru!"
            com.example.data.model.CyclePhase.OVULATION -> "Energi fisik di titik puncak, stamina dan kepercayaan diri optimal untuk aktivitas dinamis!"
            com.example.data.model.CyclePhase.LUTEAL -> "Progesteron dominan, berikan waktu relaksasi, tidur teratur, dan asupan magnesium menjelang siklus berikutnya."
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = LunaraPrimaryStrong.copy(alpha = 0.08f),
                    spotColor = LunaraPrimaryStrong.copy(alpha = 0.15f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(LunaraPinkWash, LunaraPeachSubtle.copy(alpha = 0.6f))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LunaraSurfaceLowest)
                        .shadow(2.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = LunaraPrimaryStrong,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tips Ritme Tubuh Hari Ini",
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = phaseTip,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = LunaraInkMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Medical Footnote Disclaimer
        Text(
            text = "*Semua prediksi berlabel estimasi didasarkan pada perhitungan matematis ritme tubuh dan bukan merupakan diagnosis medis profesional.",
            fontFamily = PlusJakartaSans,
            fontSize = 10.sp,
            color = LunaraInkMuted.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 68.dp)
        )
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = LunaraPrimaryStrong.copy(alpha = 0.15f),
                    spotColor = LunaraPrimaryStrong.copy(alpha = 0.25f)
                )
                .clip(CircleShape)
                .background(LunaraPinkSubtle)
                .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = LunaraPrimaryStrong,
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = LunaraInk
        )
    }
}

@Composable
private fun WellnessTile(
    title: String,
    value: String,
    tag: String,
    tagColor: Color,
    tagBg: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = LunaraPrimaryStrong.copy(alpha = 0.08f),
                spotColor = LunaraPrimaryStrong.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(LunaraSurfaceLowest)
            .border(1.dp, LunaraBorderPink.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = PlusJakartaSans,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LunaraInkMuted
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontFamily = PlusJakartaSans,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(tagBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tag,
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }
            }
        }
    }
}
