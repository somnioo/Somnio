package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SomnioColorScheme = darkColorScheme(
    primary = DreamPurple,
    onPrimary = Color.White,
    secondary = DreamIndigo,
    onSecondary = Color.White,
    tertiary = CyberPink,
    onTertiary = Color.White,
    background = DeepSpaceBlack,
    onBackground = TextPrimaryGlow,
    surface = MidnightNavy,
    onSurface = TextPrimaryGlow,
    surfaceVariant = GlassCardColor,
    onSurfaceVariant = TextSecondaryDim,
    outline = GlassCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force interstellar dark mode by default
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our tailored gothic synth aesthetics!
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SomnioColorScheme,
        typography = Typography,
        content = content
    )
}
