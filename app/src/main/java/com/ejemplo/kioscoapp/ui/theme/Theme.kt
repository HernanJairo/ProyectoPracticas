package com.ejemplo.kioscoapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KioscoDarkColorScheme = darkColorScheme(
    primary = KioscoMostaza,
    onPrimary = KioscoOnMostaza,
    primaryContainer = KioscoDorado,
    onPrimaryContainer = KioscoNegro,
    secondary = KioscoDorado,
    onSecondary = KioscoNegro,
    tertiary = KioscoDorado,
    background = KioscoNegro,
    onBackground = Color(0xFFE8E8E8),
    surface = KioscoSuperficie,
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = KioscoSuperficieVariante,
    onSurfaceVariant = KioscoTextoSecundario,
    outline = KioscoDorado,
    outlineVariant = Color(0xFF3D3D3D)
)

@Composable
fun AppKioscoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KioscoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
