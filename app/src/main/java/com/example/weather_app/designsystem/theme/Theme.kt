package com.example.weather_app.designsystem.theme

import androidx.compose.runtime.Composable
import com.example.weather_app.designsystem.colors.LocalWTColors
import com.example.weather_app.designsystem.colors.WTColors
import com.example.weather_app.designsystem.dimensions.LocalWTShapes
import com.example.weather_app.designsystem.dimensions.LocalWTSpacing
import com.example.weather_app.designsystem.dimensions.WTShapes
import com.example.weather_app.designsystem.dimensions.WTSpacing
import com.example.weather_app.designsystem.textstyle.LocalWTTypography
import com.example.weather_app.designsystem.textstyle.WTTextStyle

object Theme {
        val colors: WTColors
            @Composable
            get() = LocalWTColors.current

    val typography: WTTextStyle
        @Composable
        get() = LocalWTTypography.current

    val spacing: WTSpacing
        @Composable
        get() = LocalWTSpacing.current

    val shapes: WTShapes
        @Composable
        get() = LocalWTShapes.current
}