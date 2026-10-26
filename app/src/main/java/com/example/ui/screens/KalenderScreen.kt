package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Calendar

@Composable
fun KalenderScreen(
    viewModel: CycleViewModel,
    modifier: Modifier = Modifier
) {
    val prediction by viewModel.prediction.collectAsState()
    val cycles by viewModel.cycles.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()
    val selectedMonth by viewModel.selectedCalendarMonth.collectAsState()
    val selectedYear by viewModel.selectedCalendarYear.collectAsState()
    val scrollState = rememberScrollState()

    var isMonthlyView by remember { mutableStateOf(true) }

    val monthNames = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val currentMonthTitle = "${monthNames.getOrElse(selectedMonth - 1) { "Bulan" }} $selectedYear"

    // Real dynamic calendar metrics
    val calInstance = Calendar.getInstance().apply {
        set(Calendar.YEAR, selectedYear)
        set(Calendar.MONTH, selectedMonth - 1)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startDayOfWeek = calInstance.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sunday
    val maxDaysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH)

    val todayCal = Calendar.getInstance()
    val isCurrentYearAndMonth = (todayCal.get(Calendar.YEAR) == selectedYear && todayCal.get(Calendar.MONTH) + 1 == selectedMonth)
    val todayDayOfMonth = todayCal.get(Calendar.DAY_OF_MONTH)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LunaraCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Selector & View Toggle Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Month Switcher Pill
            Row(
                modifier = Modifier
                    .shadow(4.dp, CircleShape, ambientColor = LunaraPrimaryStrong.copy(alpha = 0.1f))
                    .clip(CircleShape)
                    .background(LunaraSurfaceLowest)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.previousMonth() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Bulan sebelumnya",
                        tint = LunaraInkMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = LunaraPrimaryStrong,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = currentMonthTitle,
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInk
                    )
                }

                IconButton(
                    onClick = { viewModel.nextMonth() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Bulan berikutnya",
                        tint = LunaraInkMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // View Toggle (Bulan / Minggu)
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LunaraPinkWash)
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isMonthlyView) LunaraSurfaceLowest else Color.Transparent)
                        .shadow(if (isMonthlyView) 2.dp else 0.dp, CircleShape)
                        .clickable { isMonthlyView = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Bulan",
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMonthlyView) LunaraPrimaryText else LunaraInkMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (!isMonthlyView) LunaraSurfaceLowest else Color.Transparent)
                        .shadow(if (!isMonthlyView) 2.dp else 0.dp, CircleShape)
                        .clickable { isMonthlyView = false }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Minggu",
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isMonthlyView) LunaraPrimaryText else LunaraInkMuted
                    )
                }
            }
        }

        // Feature: 28-Dot Cycle Matrix
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(LunaraPhaseFertile)
                            )
                            Text(
                                text = "SIKLUS SAAT INI (${prediction.averageCycleLength} HARI)",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInkMuted,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hari ke-${prediction.currentCycleDay} ",
                                fontFamily = PlusJakartaSans,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInk
                            )
                            Text(
                                text = "• ${prediction.currentPhase.displayName} (estimasi)",
                                fontFamily = PlusJakartaSans,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LunaraPrimaryText
                            )
                        }
                    }

                    // Fertile Peak Pill
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LunaraLavenderSubtle)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = LunaraSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (prediction.currentCycleDay in prediction.fertileWindowStartDay..prediction.fertileWindowEndDay) "Masa Subur" else "Estimasi",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraSecondary
                        )
                    }
                }

                // 28-Dot Matrix Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(LunaraPinkWash.copy(alpha = 0.6f))
                        .padding(12.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulseMatrix")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.25f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseScale"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (row in 0 until 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (col in 0 until 7) {
                                    val dayNum = row * 7 + col + 1
                                    CycleMatrixDot(
                                        day = dayNum,
                                        isToday = dayNum == prediction.currentCycleDay,
                                        prediction = prediction,
                                        pulseScale = pulseScale,
                                        onClick = {
                                            val logDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, dayNum.coerceAtMost(maxDaysInMonth))
                                            viewModel.openLogSheet(logDate)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = LunaraPhasePeriod, label = "Haid (H1-${prediction.averagePeriodLength})")
                    LegendItem(color = LunaraPinkSubtle, label = "Folikuler")
                    LegendItem(color = LunaraPhaseFertile, label = "Subur (H${prediction.fertileWindowStartDay}-${prediction.fertileWindowEndDay})")
                    LegendItem(color = LunaraPhasePMS, label = "PMS (H${prediction.pmsStartDay}-${prediction.pmsEndDay})")
                }
            }
        }

        // Monthly Calendar Grid
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Day of week labels
                val daysOfWeek = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    daysOfWeek.forEach { dayName ->
                        Text(
                            text = dayName,
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LunaraInkMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                // Compute total cells (weeks needed)
                val totalDays = startDayOfWeek + maxDaysInMonth
                val totalWeeks = (totalDays + 6) / 7

                for (week in 0 until totalWeeks) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (dayIdx in 0 until 7) {
                            val cellIndex = week * 7 + dayIdx
                            val dayOfMonth = cellIndex - startDayOfWeek + 1

                            if (dayOfMonth in 1..maxDaysInMonth) {
                                val cellDateStr = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, dayOfMonth)
                                val hasLog = dailyLogs.containsKey(cellDateStr)
                                val logEntry = dailyLogs[cellDateStr]
                                val isPeriodLog = (logEntry != null && logEntry.flowLevel != com.example.data.model.FlowLevel.NONE)
                                val isToday = (isCurrentYearAndMonth && dayOfMonth == todayDayOfMonth)

                                CalendarDayCell(
                                    day = dayOfMonth,
                                    isToday = isToday,
                                    isPeriod = isPeriodLog,
                                    isFertile = false,
                                    isNextPeriodEstimated = false,
                                    hasNotes = !logEntry?.privateNotes.isNullOrBlank(),
                                    onClick = {
                                        viewModel.openLogSheet(cellDateStr)
                                    }
                                )
                            } else {
                                Box(modifier = Modifier.size(36.dp, 44.dp))
                            }
                        }
                    }
                }
            }
        }

        // Predictions & Care Insight Card
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(130.dp)
                        .background(LunaraPrimaryStrong, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = LunaraPrimaryStrong,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Prediksi Siklus Berikutnya",
                                fontFamily = PlusJakartaSans,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInk
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LunaraPinkWash)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Estimasi",
                                fontFamily = PlusJakartaSans,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaraInkMuted
                            )
                        }
                    }

                    // 3 Metric Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val remainingText = if (prediction.isLate) "Terlambat ${prediction.daysLate} hari" else "${prediction.nextPeriodDaysRemaining} hari lagi"
                        PredictionMetricItem(
                            label = "Haid Berikutnya",
                            value = prediction.nextPeriodDateFormatted.ifBlank { "Segera" },
                            subtext = remainingText,
                            valueColor = LunaraPrimaryStrong,
                            subtextColor = if (prediction.isLate) LunaraPrimaryDark else LunaraInkMuted,
                            modifier = Modifier.weight(1f)
                        )
                        PredictionMetricItem(
                            label = "Rata-rata Siklus",
                            value = "${prediction.averageCycleLength} Hari",
                            subtext = if (prediction.isRegular) "Teratur" else "Bervariasi",
                            valueColor = LunaraInk,
                            subtextColor = LunaraStatusSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        PredictionMetricItem(
                            label = "Durasi Haid",
                            value = "${prediction.averagePeriodLength} Hari",
                            subtext = "Normal",
                            valueColor = LunaraInk,
                            subtextColor = LunaraInkMuted,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Care Prompt Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(LunaraLavenderSubtle.copy(alpha = 0.7f))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(LunaraSurfaceLowest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = LunaraSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Fase ${prediction.currentPhase.displayName}",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LunaraSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = prediction.currentPhase.description,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 11.sp,
                                    color = LunaraInkMuted,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Primary Bottom Action CTA
        Button(
            onClick = { viewModel.openLogSheet(viewModel.todayDateString) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = LunaraPrimaryStrong.copy(alpha = 0.25f),
                    spotColor = LunaraPrimaryStrong.copy(alpha = 0.35f)
                )
                .testTag("btn_catat_siklus_kalender"),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = LunaraPrimaryStrong,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Catat Siklus & Gejala Hari Ini",
                fontFamily = PlusJakartaSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "*Semua prediksi berlabel estimasi didasarkan pada perhitungan matematis ritme tubuh dan bukan merupakan diagnosis medis profesional.",
            fontFamily = PlusJakartaSans,
            fontSize = 10.sp,
            color = LunaraInkMuted.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(68.dp))
    }
}

@Composable
private fun CycleMatrixDot(
    day: Int,
    isToday: Boolean,
    prediction: com.example.data.model.CyclePrediction,
    pulseScale: Float,
    onClick: () -> Unit
) {
    val bg = when {
        isToday -> LunaraSecondary
        day <= prediction.averagePeriodLength -> LunaraPhasePeriod
        day in (prediction.averagePeriodLength + 1) until prediction.fertileWindowStartDay -> LunaraPinkSubtle
        day in prediction.fertileWindowStartDay..prediction.fertileWindowEndDay -> LunaraPhaseFertile.copy(alpha = 0.7f)
        day in prediction.pmsStartDay..prediction.pmsEndDay -> LunaraPhasePMS
        else -> LunaraPeachSubtle
    }
    val textColor = when {
        isToday -> Color.White
        day <= prediction.averagePeriodLength -> Color.White
        day in (prediction.averagePeriodLength + 1) until prediction.fertileWindowStartDay -> LunaraPrimaryText
        day in prediction.fertileWindowStartDay..prediction.fertileWindowEndDay -> Color.White
        day in prediction.pmsStartDay..prediction.pmsEndDay -> Color.White
        else -> LunaraTertiary
    }

    Box(
        modifier = Modifier
            .size(34.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(LunaraSecondary.copy(alpha = 0.25f))
            )
        }
        Box(
            modifier = Modifier
                .size(if (isToday) 28.dp else 24.dp)
                .clip(CircleShape)
                .background(bg)
                .then(
                    if (isToday) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$day",
                fontFamily = PlusJakartaSans,
                fontSize = if (isToday) 11.sp else 10.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontSize = 10.sp,
            color = LunaraInkMuted
        )
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isToday: Boolean,
    isPeriod: Boolean,
    isFertile: Boolean,
    isNextPeriodEstimated: Boolean,
    hasNotes: Boolean = false,
    onClick: () -> Unit
) {
    val bg = when {
        isToday -> LunaraPrimaryStrong
        isPeriod -> LunaraPinkSubtle
        isFertile -> LunaraLavenderSubtle
        isNextPeriodEstimated -> LunaraPinkTint
        else -> LunaraPinkWash.copy(alpha = 0.5f)
    }
    val textCol = when {
        isToday -> Color.White
        isPeriod -> LunaraPrimaryText
        isFertile -> LunaraSecondary
        isNextPeriodEstimated -> LunaraPrimaryStrong
        else -> LunaraInk
    }

    Box(
        modifier = Modifier
            .size(width = 38.dp, height = 46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$day",
                fontFamily = PlusJakartaSans,
                fontSize = 12.sp,
                fontWeight = if (isToday || isPeriod) FontWeight.Bold else FontWeight.Medium,
                color = textCol
            )
            Spacer(modifier = Modifier.height(2.dp))
            when {
                isToday -> {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = LunaraPinkSubtle,
                        modifier = Modifier.size(9.dp)
                    )
                }
                isPeriod -> {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(LunaraPhasePeriod)
                    )
                }
                hasNotes -> {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(LunaraSecondary)
                    )
                }
            }
        }
    }
}

@Composable
private fun PredictionMetricItem(
    label: String,
    value: String,
    subtext: String,
    valueColor: Color,
    subtextColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LunaraPinkWash)
            .padding(8.dp)
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
                color = valueColor
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtext,
                fontFamily = PlusJakartaSans,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = subtextColor
            )
        }
    }
}
