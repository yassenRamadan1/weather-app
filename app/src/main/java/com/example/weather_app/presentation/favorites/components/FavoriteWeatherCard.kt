package com.example.weather_app.presentation.favorites.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults.outlinedButtonBorder
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.designsystem.theme.WTTheme

@Composable
fun FavoriteWeatherCard(
    modifier: Modifier = Modifier,
    location: String = "Potosí,\nBolivia",
    onClickRemove: () -> Unit = {},
    weatherIconRes: Int = R.drawable.img_cloud,
    color: Color = Theme.colors.textColors.titleColor.copy(alpha = 0.07f)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        CardBackground(
            modifier = Modifier.matchParentSize(),
            backgroundColor = color
        )
        ConstraintLayout(
            modifier = Modifier
                .matchParentSize()
                .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
        ) {
            val (locRef, cloudRef, buttonRef) = createRefs()
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .constrainAs(locRef) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start)
                    },
            ) {
                Text(
                    text = location,
                    style = Theme.typography.title.copy(color = Color.White),
                    maxLines = 2
                )
            }

            OutlinedButton (
                onClick = onClickRemove,
                modifier = Modifier
                    .constrainAs(buttonRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .size(width = 56.dp, height = 16.dp),
                border = outlinedButtonBorder(true).copy(width = 1.dp, brush = Brush.linearGradient(
                    colors = listOf(
                        Theme.colors.errorColor,
                        Theme.colors.errorColor.copy(alpha = 0.7f)
                    )
                ))
                ,
                contentPadding = PaddingValues(vertical = 1.dp, horizontal = 1.dp),
            ) {
                Text(
                    text = stringResource(R.string.remove),
                    style = Theme.typography.hint.copy(fontSize = 8.sp),
                    color = Theme.colors.errorColor,
                )
            }
            Image(
                painter = painterResource(id = weatherIconRes),
                contentDescription = "Weather icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(130.dp)
                    .constrainAs(cloudRef) {
                        end.linkTo(parent.end, margin = (-8).dp)
                        top.linkTo(parent.top, margin = (-24).dp)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteWeatherCardPreview() {
    WTTheme() {
    FavoriteWeatherCard()
    }
}