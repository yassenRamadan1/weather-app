package com.example.weather_app.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.weather_app.designsystem.colors.LocalWTColors
import com.example.weather_app.designsystem.colors.darkThemeColors
import com.example.weather_app.designsystem.colors.lightThemeColors
import com.example.weather_app.designsystem.textstyle.LocalWTTypography
import com.example.weather_app.designsystem.textstyle.getWTTypography

@Composable
fun WTTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isArabic: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) darkThemeColors else lightThemeColors

    val typography = getWTTypography(isArabic = isArabic)

    CompositionLocalProvider(
        LocalWTColors provides colors,
        LocalWTTypography provides typography
    ) {
        content()
    }
}