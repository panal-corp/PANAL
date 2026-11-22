package com.panal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/* ============================================================
   PanalTheme.kt
   Paleta "panal nocturno" de Colors.xaml (.NET), permanente:
   en .NET el modo claro ya no existe, así que aquí tampoco.
   Cuando agregues la fuente Manrope (res/font), cambia
   FontFamily.SansSerif por tu FontFamily de Manrope.
   ============================================================ */

val PanalBackground = Color(0xFF0C0C0C)   /* PanalPageBackground / PanalFog */
val PanalSurface = Color(0xFF171512)      /* PanalWhite (tarjetas) */
val PanalBrandSurface = Color(0xFF231C12) /* PanalBrandSurface */
val PanalInput = Color(0xFF1F1D19)        /* PanalInput */
val PanalSoft = Color(0xFF241E15)         /* PanalSoft */
val PanalTitle = Color(0xFFF5EFE3)        /* PanalPetrol / PanalPageTitle */
val PanalBody = Color(0xFFB4A996)         /* PanalSlate / PanalPageBody */
val PanalGold = Color(0xFFE2C08C)         /* PanalEmerald / acento */
val PanalGoldSoft = Color(0xFFF3DCB6)     /* PanalMint */
val PanalGoldPressed = Color(0xFFC9A472)  /* PanalPressed */
val PanalGoldText = Color(0xFF1B1408)     /* texto sobre botones de oro */
val PanalOutline = Color(0xFF2F2A22)      /* PanalOutline */
val PanalError = Color(0xFFF0A79E)        /* PanalError */
val PanalErrorSoft = Color(0xFF2B1A17)    /* PanalErrorSoft */
val PanalOnBrand = Color(0xFFE8D9BE)      /* PanalDarkSurfaceText */

private val PanalColorScheme = darkColorScheme(
    primary = PanalGold,
    onPrimary = PanalGoldText,
    primaryContainer = PanalBrandSurface,
    onPrimaryContainer = PanalOnBrand,
    secondary = PanalGoldSoft,
    onSecondary = PanalGoldText,
    background = PanalBackground,
    onBackground = PanalTitle,
    surface = PanalSurface,
    onSurface = PanalTitle,
    surfaceVariant = PanalSoft,
    onSurfaceVariant = PanalBody,
    outline = PanalOutline,
    error = PanalError,
    onError = PanalGoldText,
    errorContainer = PanalErrorSoft,
    onErrorContainer = PanalError
)

/* Tipografía: mismos tamaños/pesos que Manrope en XAML. */
private val PanalTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 17.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp
    )
)

@Composable
fun PanalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PanalColorScheme,
        typography = PanalTypography,
        content = content
    )
}
