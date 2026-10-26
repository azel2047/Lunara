package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyLogEntry
import com.example.data.model.FlowLevel
import com.example.data.model.IntimacyType
import com.example.data.model.MoodType
import com.example.data.model.PhysicalSymptom
import com.example.ui.theme.LunaraBorderPink
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraPinkSubtle
import com.example.ui.theme.LunaraPinkTint
import com.example.ui.theme.LunaraPinkWash
import com.example.ui.theme.LunaraPrimaryDark
import com.example.ui.theme.LunaraPrimaryStrong
import com.example.ui.theme.LunaraPrimaryText
import com.example.ui.theme.LunaraStatusSuccess
import com.example.ui.theme.LunaraSurfaceLowest
import com.example.ui.theme.PlusJakartaSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogCycleBottomSheet(
    initialLog: DailyLogEntry?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (DailyLogEntry) -> Unit
) {
    var selectedFlow by remember { mutableStateOf(initialLog?.flowLevel ?: FlowLevel.NONE) }
    var selectedSymptoms by remember {
        mutableStateOf(initialLog?.symptoms ?: setOf("cramps", "cervical_fluid", "tender_breasts"))
    }
    var selectedMood by remember { mutableStateOf(initialLog?.mood ?: MoodType.HAPPY) }
    var selectedIntimacy by remember {
        mutableStateOf(initialLog?.intimacy ?: setOf(IntimacyType.HIGH, IntimacyType.PROTECTED))
    }
    var privateNotes by remember { mutableStateOf(initialLog?.privateNotes ?: "") }

    val symptomsList = listOf(
        PhysicalSymptom("cramps", "Kram Perut", "Ringan", "⚡"),
        PhysicalSymptom("headache", "Sakit Kepala", "Terasa tegang", "🤕"),
        PhysicalSymptom("fatigue", "Lelah / Lemas", "Energi turun", "💤"),
        PhysicalSymptom("bloating", "Kembung", "Terasa penuh", "🎈"),
        PhysicalSymptom("tender_breasts", "Payudara Sensitif", "Nyeri tekan", "🌸"),
        PhysicalSymptom("backache", "Nyeri Punggung", "Pegall", "🧘"),
        PhysicalSymptom("cervical_fluid", "Cairan Bening", "Khas Ovulasi", "💧"),
        PhysicalSymptom("clear_skin", "Kulit Bersih", "Tidak berjerawat", "✨"),
        PhysicalSymptom("nausea", "Mual / Pusing", "Perut sensitif", "🤢"),
        PhysicalSymptom("insomnia", "Sulit Tidur", "Tidur terganggu", "🌙")
    )

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
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(28.dp))
                Text(
                    text = "Catat Siklus & Gejala",
                    fontFamily = PlusJakartaSans,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaraInk
                )
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

            // Date & Phase Context Chip
            val chipDateText = remember(initialLog?.dateString) {
                val str = initialLog?.dateString
                if (str != null && str.contains("-")) {
                    val parts = str.split("-")
                    val y = parts.getOrNull(0)?.toIntOrNull() ?: 2024
                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 10
                    val d = parts.getOrNull(2)?.toIntOrNull() ?: 1
                    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")
                    "$d ${monthNames.getOrElse(m - 1) { "" }} $y"
                } else {
                    "Hari Ini"
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(LunaraPinkTint)
                        .border(1.dp, LunaraBorderPink, CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = LunaraPrimaryText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = chipDateText,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraPrimaryText
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(LunaraPrimaryText)
                    )
                    Text(
                        text = "Catatan Harian",
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = LunaraPrimaryDark
                    )
                }
            }

            // Section 1: Aliran Darah Haid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Aliran Darah Haid",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(Opsional)",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LunaraInkMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LunaraPinkSubtle)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = selectedFlow.displayName,
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraPrimaryText
                        )
                    }
                }

                // Flow Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FlowLevel.values().forEach { level ->
                        val isSelected = selectedFlow == level
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(16.dp), ambientColor = LunaraPrimaryStrong.copy(alpha = 0.15f))
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) LunaraPinkTint else LunaraSurfaceLowest)
                                .border(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) LunaraPrimaryStrong else LunaraBorderPink.copy(alpha = 0.5f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedFlow = level }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) LunaraPinkSubtle else LunaraPinkWash),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val dotColor = when (level) {
                                        FlowLevel.NONE -> LunaraInkMuted.copy(alpha = 0.5f)
                                        FlowLevel.SPOTTING -> Color(0xFFE57399)
                                        FlowLevel.LIGHT -> LunaraPrimaryText
                                        FlowLevel.MEDIUM -> LunaraPrimaryStrong
                                        FlowLevel.HEAVY -> LunaraPrimaryDark
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(if (level == FlowLevel.HEAVY) 10.dp else 8.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                }
                                Text(
                                    text = level.shortName,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) LunaraPrimaryDark else LunaraInkMuted
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Kondisi Fisik & Gejala Tubuh
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kondisi Fisik & Gejala Tubuh",
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInk
                    )
                    Text(
                        text = "Pilih yang dirasakan",
                        fontFamily = PlusJakartaSans,
                        fontSize = 11.sp,
                        color = LunaraInkMuted
                    )
                }

                // 2-column symptom grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    symptomsList.chunked(2).forEach { rowSymptoms ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSymptoms.forEach { symptom ->
                                val isSelected = selectedSymptoms.contains(symptom.id)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(16.dp), ambientColor = LunaraPrimaryStrong.copy(alpha = 0.12f))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) LunaraPinkTint else LunaraSurfaceLowest)
                                        .border(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) LunaraPrimaryStrong else LunaraBorderPink.copy(alpha = 0.4f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedSymptoms = if (isSelected) {
                                                selectedSymptoms - symptom.id
                                            } else {
                                                selectedSymptoms + symptom.id
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = symptom.emoji, fontSize = 16.sp)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = symptom.title,
                                                fontFamily = PlusJakartaSans,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) LunaraPrimaryDark else LunaraInk
                                            )
                                            Text(
                                                text = symptom.subtitle,
                                                fontFamily = PlusJakartaSans,
                                                fontSize = 9.sp,
                                                color = if (isSelected) LunaraPrimaryText else LunaraInkMuted
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = LunaraPrimaryStrong,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Mood & Perasaan
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mood & Perasaan",
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInk
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LunaraPinkSubtle)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${selectedMood.emoji} ${selectedMood.displayName}",
                            fontFamily = PlusJakartaSans,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraPrimaryText
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LunaraSurfaceLowest)
                        .border(1.dp, LunaraBorderPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .horizontalScroll(rememberScrollState())
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MoodType.values().forEach { mood ->
                        val isSelected = selectedMood == mood
                        Box(
                            modifier = Modifier
                                .width(68.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) LunaraPinkTint else Color.Transparent)
                                .border(
                                    if (isSelected) 1.dp else 0.dp,
                                    if (isSelected) LunaraPrimaryStrong else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedMood = mood }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(text = mood.emoji, fontSize = 20.sp)
                                Text(
                                    text = mood.displayName,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) LunaraPrimaryDark else LunaraInkMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Aktivitas & Hasrat Seksual
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aktivitas & Hasrat Seksual",
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LunaraInk
                    )
                    Text(
                        text = "Privat & Enkripsi",
                        fontFamily = PlusJakartaSans,
                        fontSize = 10.sp,
                        color = LunaraInkMuted
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IntimacyType.values().forEach { item ->
                        val isSelected = selectedIntimacy.contains(item)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) LunaraPinkTint else LunaraSurfaceLowest)
                                .border(
                                    if (isSelected) 1.2.dp else 1.dp,
                                    if (isSelected) LunaraPrimaryStrong else LunaraBorderPink.copy(alpha = 0.4f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedIntimacy = if (isSelected) {
                                        selectedIntimacy - item
                                    } else {
                                        selectedIntimacy + item
                                    }
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(text = item.emoji, fontSize = 12.sp)
                                Text(
                                    text = item.displayName,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) LunaraPrimaryDark else LunaraInk
                                )
                            }
                        }
                    }
                }
            }

            // Section 5: Catatan Pribadi
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Catatan Pribadi",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaraInk
                        )
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = LunaraInkMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "${privateNotes.length} / 250",
                        fontFamily = PlusJakartaSans,
                        fontSize = 10.sp,
                        color = LunaraInkMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LunaraSurfaceLowest)
                        .border(1.dp, LunaraBorderPink, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = privateNotes,
                            onValueChange = { if (it.length <= 250) privateNotes = it },
                            placeholder = {
                                Text(
                                    text = "Tulis catatan, asupan gizi, atau rasa tubuhmu hari ini... (hanya dapat dibaca olehmu)",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 11.sp,
                                    color = LunaraInkMuted
                                )
                            },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = PlusJakartaSans,
                                fontSize = 12.sp,
                                color = LunaraInk
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .testTag("private_notes_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(LunaraStatusSuccess)
                            )
                            Text(
                                text = "Tersimpan terenkripsi aman",
                                fontFamily = PlusJakartaSans,
                                fontSize = 10.sp,
                                color = LunaraStatusSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // CTAs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        val newEntry = DailyLogEntry(
                            dateString = initialLog?.dateString ?: "2024-10-14",
                            flowLevel = selectedFlow,
                            symptoms = selectedSymptoms,
                            mood = selectedMood,
                            intimacy = selectedIntimacy,
                            privateNotes = privateNotes
                        )
                        onSave(newEntry)
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
                        .testTag("btn_simpan_catatan"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LunaraPrimaryStrong,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Simpan Catatan",
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Batalkan Perubahan",
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = LunaraInkMuted
                    )
                }
            }
        }
    }
}
