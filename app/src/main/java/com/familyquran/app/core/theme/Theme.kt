package com.familyquran.app.core.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val QuranLightColorScheme = lightColorScheme(
    primary = QuranThemeColors.emerald,
    onPrimary = QuranThemeColors.card,
    primaryContainer = QuranThemeColors.emeraldSoft,
    secondary = QuranThemeColors.gold,
    onSecondary = QuranThemeColors.ink,
    secondaryContainer = QuranThemeColors.goldSoft,
    background = QuranThemeColors.ivory,
    onBackground = QuranThemeColors.ink,
    surface = QuranThemeColors.card,
    onSurface = QuranThemeColors.ink,
    surfaceVariant = QuranThemeColors.goldSoft,
    outline = QuranThemeColors.line
)

@Composable
fun RainaraQuranTheme(content: @Composable () -> Unit) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = QuranThemeColors.ivory.toArgb()
            window.navigationBarColor = QuranThemeColors.ivory.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = QuranLightColorScheme,
        typography = QuranTypography,
        content = content
    )
}
