package com.example.weather_app.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.weather_app.designsystem.colors.LocalWTColors
import com.example.weather_app.designsystem.colors.darkThemeColors
import com.example.weather_app.designsystem.colors.lightThemeColors
import com.example.weather_app.designsystem.dimensions.LocalWTShapes
import com.example.weather_app.designsystem.dimensions.LocalWTSpacing
import com.example.weather_app.designsystem.dimensions.WTShapes
import com.example.weather_app.designsystem.dimensions.WTSpacing
import com.example.weather_app.designsystem.textstyle.LocalWTTypography
import com.example.weather_app.designsystem.textstyle.getWTTypography

@Composable
fun WTTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isArabic: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors     = if (isDarkTheme) darkThemeColors else lightThemeColors
    val typography = getWTTypography(isArabic = isArabic)
    val spacing    = WTSpacing()
    val shapes     = WTShapes()
    val layoutDir  = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalWTColors        provides colors,
        LocalWTTypography    provides typography,
        LocalWTSpacing       provides spacing,
        LocalWTShapes        provides shapes,
        LocalLayoutDirection provides layoutDir,
    ) {
        content()
    }
}
