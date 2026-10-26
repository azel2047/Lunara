package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LunaraPrimaryStrong,
    onPrimary = Color.White,
    primaryContainer = LunaraPinkSubtle,
    onPrimaryContainer = LunaraPrimaryDark,
    secondary = LunaraSecondary,
    onSecondary = Color.White,
    secondaryContainer = LunaraLavenderSubtle,
    onSecondaryContainer = LunaraSecondary,
    tertiary = LunaraTertiary,
    onTertiary = Color.White,
    tertiaryContainer = LunaraPeachSubtle,
    onTertiaryContainer = LunaraTertiary,
    background = LunaraCanvas,
    onBackground = LunaraOnSurface,
    surface = LunaraSurface,
    onSurface = LunaraOnSurface,
    surfaceVariant = LunaraSurfaceContainer,
    onSurfaceVariant = LunaraOnSurfaceVariant,
    outline = LunaraBorderPink
)

private val DarkColorScheme = darkColorScheme(
    primary = LunaraPrimaryStrong,
    onPrimary = Color.White,
    primaryContainer = LunaraPrimaryDark,
    onPrimaryContainer = LunaraPinkSubtle,
    secondary = LunaraSecondary,
    onSecondary = Color.White,
    secondaryContainer = LunaraLavenderSubtle,
    onSecondaryContainer = LunaraSecondary,
    tertiary = LunaraTertiary,
    onTertiary = Color.White,
    tertiaryContainer = LunaraPeachSubtle,
    onTertiaryContainer = LunaraTertiary,
    background = Color(0xFF1E1A24),
    onBackground = Color(0xFFF1EEF8),
    surface = Color(0xFF282330),
    onSurface = Color(0xFFF1EEF8),
    surfaceVariant = Color(0xFF342E3E),
    onSurfaceVariant = Color(0xFFD4C8D8),
    outline = LunaraBorderPink
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
