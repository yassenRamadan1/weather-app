package com.example.weather_app.designsystem.textstyle

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

data class WTTextStyle(
    val headline: TextStyle,
    val title: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val hint: TextStyle,
    val hintMedium: TextStyle,
    val bodySmall: TextStyle,
)
val LocalWTTypography = staticCompositionLocalOf {
    getWTTypography(isArabic = false)
}