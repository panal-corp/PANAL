package com.tdp.panal.ui.theme

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
   MosaicTheme.kt
   Paleta "panal nocturno" de Colors.xaml (.NET), permanente:
   en .NET el modo claro ya no existe, así que aquí tampoco.
   Cuando agregues la fuente Manrope (res/font), cambia
   FontFamily.SansSerif por tu FontFamily de Manrope.
   ============================================================ */

val MosaicBackground = Color(0xFF0C0C0C)   /* MosaicPageBackground / MosaicFog */
val MosaicSurface = Color(0xFF171512)      /* MosaicWhite (tarjetas) */
val MosaicBrandSurface = Color(0xFF231C12) /* MosaicBrandSurface */
val MosaicInput = Color(0xFF1F1D19)        /* MosaicInput */
val MosaicSoft = Color(0xFF241E15)         /* MosaicSoft */
val MosaicTitle = Color(0xFFF5EFE3)        /* MosaicPetrol / MosaicPageTitle */
val MosaicBody = Color(0xFFB4A996)         /* MosaicSlate / MosaicPageBody */
val MosaicGold = Color(0xFFE2C08C)         /* MosaicEmerald / acento */
val MosaicGoldSoft = Color(0xFFF3DCB6)     /* MosaicMint */
val MosaicGoldPressed = Color(0xFFC9A472)  /* MosaicPressed */
val MosaicGoldText = Color(0xFF1B1408)     /* texto sobre botones de oro */
val MosaicOutline = Color(0xFF2F2A22)      /* MosaicOutline */
val MosaicError = Color(0xFFF0A79E)        /* MosaicError */
val MosaicErrorSoft = Color(0xFF2B1A17)    /* MosaicErrorSoft */
val MosaicOnBrand = Color(0xFFE8D9BE)      /* MosaicDarkSurfaceText */

private val MosaicColorScheme = darkColorScheme(
    primary = MosaicGold,
    onPrimary = MosaicGoldText,
    primaryContainer = MosaicBrandSurface,
    onPrimaryContainer = MosaicOnBrand,
    secondary = MosaicGoldSoft,
    onSecondary = MosaicGoldText,
    background = MosaicBackground,
    onBackground = MosaicTitle,
    surface = MosaicSurface,
    onSurface = MosaicTitle,
    surfaceVariant = MosaicSoft,
    onSurfaceVariant = MosaicBody,
    outline = MosaicOutline,
    error = MosaicError,
    onError = MosaicGoldText,
    errorContainer = MosaicErrorSoft,
    onErrorContainer = MosaicError
)

/* Tipografía: mismos tamaños/pesos que Manrope en XAML. */
private val MosaicTypography = Typography(
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
fun MosaicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MosaicColorScheme,
        typography = MosaicTypography,
        content = content
    )
}
