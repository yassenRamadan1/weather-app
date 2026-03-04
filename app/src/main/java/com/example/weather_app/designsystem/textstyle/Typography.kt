package com.example.weather_app.designsystem.textstyle

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun getWTTypography(isArabic: Boolean): WTTextStyle {
    val currentFontFamily = if (isArabic) CairoFontFamily else UrbanistFontFamily
    return WTTextStyle(
        headline = TextStyle(
            fontFamily = currentFontFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        ),
        title = TextStyle(
            fontFamily = currentFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        ),
        bodyLarge = TextStyle(
            fontFamily = currentFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyMedium = TextStyle(
            fontFamily = currentFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        hint = TextStyle(
            fontFamily = currentFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        )
    )
}