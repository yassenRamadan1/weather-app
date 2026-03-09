package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.weather_app.R

@Composable
fun WeatherIconImage(
    iconCode: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    if (iconCode.isBlank()||iconCode.isEmpty()){
        Image(
            painter = painterResource(R.drawable.weather_icon_0_n),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    }else{
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${iconCode}@4x.png",
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    }

}
