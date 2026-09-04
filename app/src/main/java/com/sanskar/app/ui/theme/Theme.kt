package com.sanskar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Saffron / temple-inspired palette
val Saffron = Color(0xFFB93E0A)
val SaffronDark = Color(0xFF8A2D06)
val Marigold = Color(0xFFE8A100)
val Maroon = Color(0xFF7B1E3B)
val Cream = Color(0xFFFFF8F1)
val CreamCard = Color(0xFFFFEFE0)
val TextDark = Color(0xFF3A2A20)

private val SanskarColorScheme = lightColorScheme(
    primary = Saffron,
    onPrimary = Color.White,
    primaryContainer = CreamCard,
    onPrimaryContainer = SaffronDark,
    secondary = Marigold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE9BD),
    onSecondaryContainer = Color(0xFF5C4300),
    tertiary = Maroon,
    onTertiary = Color.White,
    background = Cream,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    surfaceVariant = CreamCard,
    onSurfaceVariant = Color(0xFF6E5A4B),
    outline = Color(0xFFC9A88F)
)

@Composable
fun SanskarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SanskarColorScheme,
        content = content
    )
}
