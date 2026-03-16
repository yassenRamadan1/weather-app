package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun WeeklyTemperatureLineChart(
    dailyMaxTemps: List<Double>,
    daysOfWeek: List<String>,
    modifier: Modifier = Modifier
) {
    val lineParameters = listOf(
        LineParameters(
            label = stringResource(R.string.temperature_for_5_days),
            data = dailyMaxTemps,
            lineColor = Theme.colors.primary,
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        )
    )
    Box(
        modifier = modifier
            .clip(Theme.shapes.medium)
            .background(Theme.colors.textColors.titleColor.copy(alpha = 0.07f))
            .padding(horizontal = Theme.spacing.small, vertical = Theme.spacing.small),
    ) {
        LineChart(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            linesParameters = lineParameters,
            isGrid = true,
            xAxisData = daysOfWeek,
            animateChart = true,
            showGridWithSpacer = true,
            yAxisRange = 5,
            oneLineChart = false,
            yAxisStyle = TextStyle(
                fontSize = 10.sp,
                color = Color.Gray,
            ),
            xAxisStyle = TextStyle(
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.W400
            ),
            gridOrientation = GridOrientation.VERTICAL,
            descriptionStyle = TextStyle(
                fontSize = 16.sp,
                color = Theme.colors.textColors.titleColor,
                fontFamily = Theme.typography.bodyLarge.fontFamily,
            ),

            )
    }

}