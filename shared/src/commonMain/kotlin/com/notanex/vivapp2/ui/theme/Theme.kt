package com.notanex.vivapp2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = OnPrimary,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface
)

@Composable
internal expect fun SystemAppearance(isDark: Boolean, statusBarColor: Color)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = Background,
    surface = Surface,
    onPrimary = OnPrimary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    secondary = Secondary,
    tertiary = Tertiary
)

@Composable
fun VivappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    SystemAppearance(isDark = darkTheme, statusBarColor = colorScheme.primary)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
