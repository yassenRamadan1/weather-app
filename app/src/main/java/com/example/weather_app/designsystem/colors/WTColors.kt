package com.example.weather_app.designsystem.colors

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


data class WTColors(
    val primary: Color,
    val gradientBackground: BackgroundColors,
    val textColors: TextColors,
    val errorColor: Color,
    val successColor: Color,
    val warningColor: Color,
    val primaryIconColor: Color
)

data class TextColors(
    val titleColor: Color,
    val bodyColor: Color,
    val hintColor: Color
)

data class BackgroundColors(
    val gradientBackgroundStart: Color,
    val gradientBackgroundEnd: Color
)

val LocalWTColors = staticCompositionLocalOf {
    darkThemeColors
}