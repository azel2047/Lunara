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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.DailyLogEntry
import com.example.data.model.FlowLevel
import com.example.data.model.MoodType
import com.example.ui.CycleViewModel
import com.example.ui.components.ClayCard
import com.example.ui.theme.LunaraBorderPink
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraLavenderSubtle
import com.example.ui.theme.LunaraPeachSubtle
import com.example.ui.theme.LunaraPhaseFertile
import com.example.ui.theme.LunaraPhasePMS
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
fun SiklusSayaScreen(
    viewModel: CycleViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val prediction by viewModel.prediction.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()
    val cycles by viewModel.cycles.collectAsState()

    var selectedFilter by remember { mutableStateOf("Semua") }

    // Predefined symptom display map
    val symptomLabels = mapOf(
        "cramps" to Pair("Kram Perut", "⚡"),
        "headache" to Pair("Sakit Kepala", "🤕"),
        "fatigue" to Pair("Lelah / Lemas", "💤"),
        "bloating" to Pair("Kembung", "🎈"),
        "tender_breasts" to Pair("Payudara Sensitif", "🌸"),
        "backache" to Pair("Nyeri Punggung", "🧘"),
        "cervical_fluid" to Pair("Cairan Bening", "💧"),
        "clear_skin" to Pair("Kulit Bersih", "✨")
    )

    // Calculate symptom frequencies
    val symptomCounts = mutableMapOf<String, Int>()
    dailyLogs.values.forEach { log ->
        log.symptoms.forEach { sym ->
            symptomCounts[sym] = (symptomCounts[sym] ?: 0) + 1
        }
    }

    // Sort logs descending by date
    val sortedLogs = dailyLogs.values.sortedByDescending { it.dateString }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LunaraCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = "Siklus Saya",
                    fontFamily = PlusJakartaSans,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
            }

            Button(
                onClick = { viewModel.openLogSheet(viewModel.todayDateString) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LunaraPrimaryStrong,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("btn_catat_siklus_saya")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Catat Hari Ini",
                    fontFamily = PlusJakartaSans,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Cycle Summary Card
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SIKLUS SAAT INI",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInkMuted,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hari ke-${prediction.currentCycleDay} ",
                                fontFamily = PlusJakartaSans,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInk
                            )
                            Text(
                                text = "(${prediction.currentPhase.displayName})",
                                fontFamily = PlusJakartaSans,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LunaraPrimaryText
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LunaraPinkWash)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${cycles.size} Siklus Tersimpan",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraPrimaryStrong
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        label = "Rata-rata Siklus",
                        value = "${prediction.averageCycleLength} Hari",
                        subtext = "Teratur (±1 hari)",
                        color = LunaraInk,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Durasi Haid",
                        value = "${prediction.averagePeriodLength} Hari",
                        subtext = "Rentang normal",
                        color = LunaraPrimaryStrong,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Haid Berikutnya",
                        value = prediction.nextPeriodDateFormatted,
                        subtext = "${prediction.nextPeriodDaysRemaining} hari lagi",
                        color = LunaraSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Visual Chart of Symptoms & Moods
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = LunaraPrimaryStrong,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Frekuensi Gejala & Suasana Hati",
                            fontFamily = PlusJakartaSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                    }
                    Text(
                        text = "Grafik Siklus",
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        color = LunaraInkMuted
                    )
                }

                // Symptom Frequency Bars
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val maxCount = (symptomCounts.values.maxOrNull() ?: 1).coerceAtLeast(1)

                    symptomLabels.forEach { (key, pair) ->
                        val count = symptomCounts[key] ?: 0
                        val fraction = count.toFloat() / maxCount.toFloat()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${pair.second} ${pair.first}",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                color = LunaraInk,
                                modifier = Modifier.width(130.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(LunaraPinkWash)
                            ) {
                                if (count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction.coerceIn(0.1f, 1f))
                                            .height(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    listOf(LunaraPinkSubtle, LunaraPrimaryStrong)
                                                )
                                            )
                                    )
                                }
                            }

                            Text(
                                text = "${count}x",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (count > 0) LunaraPrimaryStrong else LunaraInkMuted,
                                modifier = Modifier.width(24.dp)
                            )
                        }
                    }
                }

                // Mood Distribution Breakdown
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "DISTRIBUSI SUASANA HATI (MOOD)",
                        fontFamily = PlusJakartaSans,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInkMuted,
                        letterSpacing = 0.8.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MoodDistributionBadge(emoji = "✨", label = "Bahagia", percent = "45%", color = LunaraPrimaryStrong, modifier = Modifier.weight(1f))
                        MoodDistributionBadge(emoji = "🌸", label = "Tenang", percent = "25%", color = LunaraSecondary, modifier = Modifier.weight(1f))
                        MoodDistributionBadge(emoji = "🥺", label = "Sensitif", percent = "15%", color = LunaraTertiary, modifier = Modifier.weight(1f))
                        MoodDistributionBadge(emoji = "🌧️", label = "Lelah", percent = "15%", color = LunaraPhasePMS, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Section: Chronological Timeline of Symptoms & Moods
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Garis Waktu Catatan Harian",
                    fontFamily = PlusJakartaSans,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
                Text(
                    text = "${sortedLogs.size} Catatan",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    color = LunaraInkMuted
                )
            }

            // Timeline Items
            if (sortedLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LunaraSurfaceLowest)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada catatan gejala atau siklus. Mulai catat dengan tombol di atas!",
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = LunaraInkMuted
                    )
                }
            } else {
                sortedLogs.forEach { log ->
                    TimelineLogCard(
                        log = log,
                        symptomLabels = symptomLabels,
                        onClick = { viewModel.openLogSheet(log.dateString) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(68.dp))
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LunaraPinkWash)
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = label,
                fontFamily = PlusJakartaSans,
                fontSize = 10.sp,
                color = LunaraInkMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = PlusJakartaSans,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtext,
                fontFamily = PlusJakartaSans,
                fontSize = 9.sp,
                color = LunaraInkMuted
            )
        }
    }
}

@Composable
private fun MoodDistributionBadge(
    emoji: String,
    label: String,
    percent: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LunaraPinkWash)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = percent,
                fontFamily = PlusJakartaSans,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontFamily = PlusJakartaSans,
                fontSize = 9.sp,
                color = LunaraInkMuted
            )
        }
    }
}

@Composable
private fun TimelineLogCard(
    log: DailyLogEntry,
    symptomLabels: Map<String, Pair<String, String>>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = LunaraPrimaryStrong.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(18.dp))
            .background(LunaraSurfaceLowest)
            .border(1.dp, LunaraBorderPink.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row: Date, Flow, Mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (log.flowLevel != FlowLevel.NONE) LunaraPinkSubtle else LunaraLavenderSubtle
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${log.dayOfMonth}",
                            fontFamily = PlusJakartaSans,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.flowLevel != FlowLevel.NONE) LunaraPrimaryDark else LunaraSecondary
                        )
                    }
                    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")
                    val dateFormatted = "${log.dayOfMonth} ${monthNames.getOrElse(log.month - 1) { "" }} ${log.year}"
                    Column {
                        Text(
                            text = dateFormatted,
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                        Text(
                            text = "Hari ke-${log.dayOfMonth}",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            color = LunaraInkMuted
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (log.flowLevel != FlowLevel.NONE) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LunaraPinkTint)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Haid: ${log.flowLevel.displayName}",
                                fontFamily = PlusJakartaSans,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraPrimaryText
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LunaraPinkWash)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${log.mood.emoji} ${log.mood.displayName}",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                    }
                }
            }

            // Symptoms Chips Row
            if (log.symptoms.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    log.symptoms.forEach { symKey ->
                        val pair = symptomLabels[symKey]
                        if (pair != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LunaraPinkTint)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${pair.second} ${pair.first}",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 10.sp,
                                    color = LunaraPrimaryDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Private Notes Snippet if present
            if (!log.privateNotes.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(LunaraPinkWash.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = LunaraInkMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = log.privateNotes,
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
