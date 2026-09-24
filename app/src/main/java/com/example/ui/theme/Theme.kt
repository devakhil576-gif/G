package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ImperialGold,
    onPrimary = DeepNight,
    primaryContainer = SurfaceHigh,
    onPrimaryContainer = ImperialGoldLight,
    secondary = RubyRose,
    onSecondary = Color.White,
    secondaryContainer = RubyRoseContainer,
    onSecondaryContainer = Color(0xFFFFD9DF),
    tertiary = MysticEmerald,
    onTertiary = Color.White,
    tertiaryContainer = MysticEmeraldContainer,
    onTertiaryContainer = Color(0xFFA5F3DC),
    background = DeepNight,
    onBackground = PoeticIvory,
    surface = MidnightPlum,
    onSurface = PoeticIvory,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = PoeticIvoryMuted,
    outline = BorderSubtle,
    outlineVariant = SurfaceElevated
)

private val LightColorScheme = darkColorScheme(
    // Defaulting to the rich atmospheric nighttime aesthetic
    primary = ImperialGold,
    onPrimary = DeepNight,
    primaryContainer = SurfaceHigh,
    onPrimaryContainer = ImperialGoldLight,
    secondary = RubyRose,
    onSecondary = Color.White,
    secondaryContainer = RubyRoseContainer,
    onSecondaryContainer = Color(0xFFFFD9DF),
    tertiary = MysticEmerald,
    onTertiary = Color.White,
    tertiaryContainer = MysticEmeraldContainer,
    onTertiaryContainer = Color(0xFFA5F3DC),
    background = DeepNight,
    onBackground = PoeticIvory,
    surface = MidnightPlum,
    onSurface = PoeticIvory,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = PoeticIvoryMuted,
    outline = BorderSubtle,
    outlineVariant = SurfaceElevated
)

@Composable
fun SukhanTheme(
    darkTheme: Boolean = true, // Luxury dark atmosphere default
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SukhanTheme(darkTheme = true, content = content)
}

