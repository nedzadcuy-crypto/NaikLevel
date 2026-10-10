package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = BlueSecondarySurface,
    onPrimaryContainer = TextPrimary,
    secondary = BluePrimaryLight,
    onSecondary = SurfaceWhite,
    secondaryContainer = BlueSoftBg,
    onSecondaryContainer = BluePrimaryDark,
    tertiary = SuccessGreen,
    onTertiary = SurfaceWhite,
    tertiaryContainer = SuccessContainer,
    onTertiaryContainer = TextPrimary,
    background = BlueUltraLightBg,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BlueSoftBg,
    onSurfaceVariant = TextSecondary,
    outline = OutlineVariantColor,
    outlineVariant = BorderSubtle,
    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = ErrorContainer,
    onErrorContainer = TextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryLight,
    onPrimary = SurfaceWhite,
    primaryContainer = BluePrimaryDark,
    onPrimaryContainer = BlueSecondarySurface,
    secondary = BlueSecondarySurface,
    onSecondary = TextPrimary,
    background = TextPrimary,
    onBackground = SurfaceWhite,
    surface = Color(0xFF1E293B),
    onSurface = SurfaceWhite,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF475569),
    tertiary = SuccessGreen
)

@Composable
fun NaikLevelTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = NaikLevelTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
