package com.hafiz.nimmaguru.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Saffron-inspired palette for Karnataka community feel
val SaffronOrange = Color(0xFFFF6B2B)
val DeepOrange = Color(0xFFE64A00)
val LightOrange = Color(0xFFFFE0CC)
val TealGreen = Color(0xFF00695C)
val LightTeal = Color(0xFFB2DFDB)
val WarmWhite = Color(0xFFFFFBF7)
val DarkBrown = Color(0xFF3E2723)

private val LightColorScheme = lightColorScheme(
    primary = SaffronOrange,
    onPrimary = Color.White,
    primaryContainer = LightOrange,
    onPrimaryContainer = DeepOrange,
    secondary = TealGreen,
    onSecondary = Color.White,
    secondaryContainer = LightTeal,
    onSecondaryContainer = TealGreen,
    background = WarmWhite,
    onBackground = DarkBrown,
    surface = Color.White,
    onSurface = DarkBrown,
    surfaceVariant = Color(0xFFF5EDE6),
    error = Color(0xFFB00020)
)

@Composable
fun NimmaGuruTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(
            // Large fonts for elderly users — minimum 18sp body
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(18f, androidx.compose.ui.unit.TextUnitType.Sp)),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp)),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(22f, androidx.compose.ui.unit.TextUnitType.Sp)),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp))
        ),
        content = content
    )
}
