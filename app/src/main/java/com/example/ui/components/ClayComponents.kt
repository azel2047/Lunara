package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LunaraCanvas
import com.example.ui.theme.LunaraInk
import com.example.ui.theme.LunaraInkMuted
import com.example.ui.theme.LunaraPhaseFertile
import com.example.ui.theme.LunaraPhasePMS
import com.example.ui.theme.LunaraPhasePeriod
import com.example.ui.theme.LunaraPinkSubtle
import com.example.ui.theme.LunaraPinkTint
import com.example.ui.theme.LunaraPinkWash
import com.example.ui.theme.LunaraPrimaryStrong
import com.example.ui.theme.LunaraSurfaceLowest
import com.example.ui.theme.PlusJakartaSans

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    backgroundColor: Color = LunaraSurfaceLowest,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = LunaraPrimaryStrong.copy(alpha = 0.15f),
                spotColor = LunaraPrimaryStrong.copy(alpha = 0.25f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(backgroundColor, LunaraPinkWash.copy(alpha = 0.35f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.8f), shape)
    ) {
        // Specular inner top highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.85f), Color.Transparent)
                    )
                )
        )
        content()
    }
}

@Composable
fun Glossy3DDroplet(
    modifier: Modifier = Modifier.size(96.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dropletFloat")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dropletFloatY"
    )

    Canvas(modifier = modifier.offset(y = offsetY.dp)) {
        val w = size.width
        val h = size.height

        // Outer soft glow shadow
        drawCircle(
            color = LunaraPrimaryStrong.copy(alpha = 0.18f),
            radius = w * 0.45f,
            center = Offset(w * 0.5f, h * 0.65f)
        )

        // Glossy 3D Droplet Path
        val dropletPath = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            cubicTo(
                w * 0.5f, h * 0.08f,
                w * 0.16f, h * 0.52f,
                w * 0.16f, h * 0.68f
            )
            cubicTo(
                w * 0.16f, h * 0.88f,
                w * 0.31f, h * 0.94f,
                w * 0.5f, h * 0.94f
            )
            cubicTo(
                w * 0.69f, h * 0.94f,
                w * 0.84f, h * 0.88f,
                w * 0.84f, h * 0.68f
            )
            cubicTo(
                w * 0.84f, h * 0.52f,
                w * 0.5f, h * 0.08f,
                w * 0.5f, h * 0.08f
            )
            close()
        }

        // 3D Clay Pink gradient fill
        val dropGradient = Brush.linearGradient(
            colors = listOf(Color(0xFFFFD1DF), Color(0xFFF78CA9), Color(0xFFEB5681)),
            start = Offset(w * 0.2f, h * 0.1f),
            end = Offset(w * 0.8f, h * 0.9f)
        )
        drawPath(path = dropletPath, brush = dropGradient)

        // Primary specular glossy highlight
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.1f), Color.Transparent),
                center = Offset(w * 0.38f, h * 0.44f),
                radius = w * 0.22f
            ),
            topLeft = Offset(w * 0.30f, h * 0.36f),
            size = Size(w * 0.22f, h * 0.32f)
        )

        // Accent specular sparkle
        drawOval(
            color = Color.White.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.42f, h * 0.28f),
            size = Size(w * 0.08f, h * 0.14f)
        )
    }
}

@Composable
fun HormoneWaveChart(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(180.dp),
    activeDay: Int = 14
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background guidelines
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        drawLine(
            color = Color(0xFFEFECF9),
            start = Offset(10f, h * 0.25f),
            end = Offset(w - 10f, h * 0.25f),
            strokeWidth = 1f,
            pathEffect = dashEffect
        )
        drawLine(
            color = Color(0xFFEFECF9),
            start = Offset(10f, h * 0.55f),
            end = Offset(w - 10f, h * 0.55f),
            strokeWidth = 1f,
            pathEffect = dashEffect
        )
        drawLine(
            color = Color(0xFFEFECF9),
            start = Offset(10f, h * 0.85f),
            end = Offset(w - 10f, h * 0.85f),
            strokeWidth = 1f,
            pathEffect = dashEffect
        )

        // Ovulation highlight band (days 12-16)
        val ovStart = w * 0.38f
        val ovWidth = w * 0.18f
        drawRoundRect(
            color = LunaraPhaseFertile.copy(alpha = 0.12f),
            topLeft = Offset(ovStart, 10f),
            size = Size(ovWidth, h - 20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
        )

        // Estrogen path (Pink)
        val estrogenPath = Path().apply {
            moveTo(10f, h * 0.80f)
            cubicTo(w * 0.2f, h * 0.80f, w * 0.32f, h * 0.70f, w * 0.42f, h * 0.20f)
            cubicTo(w * 0.48f, h * 0.15f, w * 0.54f, h * 0.45f, w * 0.60f, h * 0.65f)
            cubicTo(w * 0.70f, h * 0.75f, w * 0.85f, h * 0.45f, w * 0.95f, h * 0.85f)
        }
        drawPath(
            path = estrogenPath,
            color = LunaraPhasePeriod,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Progesterone path (Lavender)
        val progPath = Path().apply {
            moveTo(10f, h * 0.88f)
            cubicTo(w * 0.3f, h * 0.88f, w * 0.5f, h * 0.85f, w * 0.60f, h * 0.75f)
            cubicTo(w * 0.72f, h * 0.28f, w * 0.85f, h * 0.30f, w * 0.95f, h * 0.85f)
        }
        drawPath(
            path = progPath,
            color = LunaraPhaseFertile,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
            )
        )

        // LH Surge path (Peach)
        val lhPath = Path().apply {
            moveTo(10f, h * 0.90f)
            cubicTo(w * 0.30f, h * 0.90f, w * 0.42f, h * 0.90f, w * 0.46f, h * 0.18f)
            cubicTo(w * 0.49f, h * 0.12f, w * 0.52f, h * 0.60f, w * 0.56f, h * 0.90f)
            lineTo(w * 0.95f, h * 0.90f)
        }
        drawPath(
            path = lhPath,
            color = LunaraPhasePMS,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Day 14 active vertical indicator
        val activeX = w * 0.47f
        drawLine(
            color = LunaraPrimaryStrong,
            start = Offset(activeX, 20f),
            end = Offset(activeX, h - 10f),
            strokeWidth = 1.8.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
        )

        // Glowing Current Day Dot
        drawCircle(
            color = LunaraPinkSubtle.copy(alpha = 0.7f),
            radius = 10.dp.toPx(),
            center = Offset(activeX, h * 0.20f)
        )
        drawCircle(
            color = LunaraPrimaryStrong,
            radius = 5.dp.toPx(),
            center = Offset(activeX, h * 0.20f)
        )
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = Offset(activeX, h * 0.20f)
        )
    }
}

@Composable
fun LunaraFloatingNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(68.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = LunaraPrimaryStrong.copy(alpha = 0.18f),
                    spotColor = LunaraPrimaryStrong.copy(alpha = 0.28f)
                )
                .clip(CircleShape)
                .background(LunaraSurfaceLowest.copy(alpha = 0.96f))
                .border(1.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                label = "Beranda",
                icon = Icons.Default.Home,
                selected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                testTag = "nav_beranda"
            )
            NavBarItem(
                label = "Siklus",
                icon = Icons.Default.WaterDrop,
                selected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                testTag = "nav_siklus_saya"
            )
            NavBarItem(
                label = "Kalender",
                icon = Icons.Default.CalendarMonth,
                selected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                testTag = "nav_kalender"
            )
            NavBarItem(
                label = "Wawasan",
                icon = Icons.Default.Insights,
                selected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                testTag = "nav_wawasan"
            )
            NavBarItem(
                label = "Profil",
                icon = Icons.Default.Person,
                selected = selectedIndex == 4,
                onClick = { onTabSelected(4) },
                testTag = "nav_profil"
            )
        }
    }
}

@Composable
private fun RowScope.NavBarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = if (selected) LunaraPrimaryStrong else LunaraInkMuted

    Column(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 28.dp),
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
    }
}
