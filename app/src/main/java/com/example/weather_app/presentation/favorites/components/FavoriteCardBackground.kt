package com.example.weather_app.presentation.favorites.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun CardBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colors.textColors.titleColor.copy(alpha = 0.07f)
) {
    Canvas(
        modifier = modifier,
    ) {
        if (size.width == 0f || size.height == 0f) return@Canvas

        val w = size.width
        val h = size.height
        val slantDrop = h * 0.30f

        val tlRadius = w * 0.15f
        val trRadius = w * 0.08f
        val brRadius = w * 0.08f
        val blRadius = w * 0.08f

        val slantSlope = slantDrop / w

        val path = Path().apply {
            moveTo(0f, tlRadius)

            val startSlantX = tlRadius
            val startSlantY = startSlantX * slantSlope
            quadraticTo(0f, 0f, startSlantX, startSlantY)

            val endSlantX = w - trRadius
            val endSlantY = endSlantX * slantSlope
            lineTo(endSlantX, endSlantY)

            quadraticTo(w, slantDrop, w, slantDrop + trRadius)

            lineTo(w, h - brRadius)

            quadraticTo(w, h, w - brRadius, h)
            lineTo(blRadius, h)

            quadraticTo(0f, h, 0f, h - blRadius)

            close()
        }

        drawPath(
            path = path,
            color = backgroundColor,
            style = Fill
        )
    }
}

@Preview
@Composable
fun CardBackgroundPreview() {
    Box(
        modifier = Modifier.size(300.dp, 150.dp)
    ) {
        CardBackground(modifier = Modifier.fillMaxSize())
    }
}