package com.example.weather_app.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.example.weather_app.designsystem.utils.updateLocale
import com.example.weather_app.domain.entity.AppLanguage

@Composable
fun WTTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isArabic: Boolean = false,
    language: AppLanguage = AppLanguage.ENGLISH,
    content: @Composable () -> Unit,
) {
    val colors     = if (isDarkTheme) darkThemeColors else lightThemeColors
    val typography = getWTTypography(isArabic = isArabic)
    val spacing    = WTSpacing()
    val shapes     = WTShapes()
    val layoutDir  = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val (localizedContext, localizedConfiguration) = remember(language) {
        val newContext = context.updateLocale(language.locale)
        val newConfig = android.content.res.Configuration(configuration).apply {
            setLocale(language.locale)
            setLayoutDirection(language.locale)
        }
        newContext to newConfig
    }

    CompositionLocalProvider(
        LocalContext         provides localizedContext,
        LocalConfiguration   provides localizedConfiguration,
        LocalWTColors        provides colors,
        LocalWTTypography    provides typography,
        LocalWTSpacing       provides spacing,
        LocalWTShapes        provides shapes,
        LocalLayoutDirection provides layoutDir,
    ) {
        content()
    }
}
