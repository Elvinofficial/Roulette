
package com.example.roulette.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RouletteColorScheme = lightColorScheme(
    primary = Color(0xFF242424),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFE8E8E8),
    onPrimaryContainer = Color(0xFF242424),

    secondary = Color(0xFF606060),
    onSecondary = Color.White,

    background = Color.White,
    onBackground = Color(0xFF242424),

    surface = Color.White,
    onSurface = Color(0xFF242424),

    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF444444),

    error = Color(0xFFB3261E),
    onError = Color.White
)

@Composable
fun RouletteTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RouletteColorScheme,
        typography = Typography,
        content = content
    )
}
