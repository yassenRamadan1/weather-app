package com.example.weather_app.data.weather

import com.example.weather_app.data.weather.remote.dto.ForecastItemDto
import com.example.weather_app.data.weather.remote.dto.ForecastResponseDto
import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.entity.HourlyWeather
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun ForecastItemDto.toHourlyWeather(): HourlyWeather = HourlyWeather(
    timestamp = dt,
    temperature = main.temp,
    iconCode = weather.firstOrNull()?.icon ?: "",
    description = weather.firstOrNull()?.description ?: "",
    windSpeed = wind.speed,
    humidity = main.humidity,
    pop = pop
)

fun ForecastResponseDto.toHourlyList(): List<HourlyWeather> =
    list.map { it.toHourlyWeather() }

fun ForecastResponseDto.toDailyForecasts(): List<DailyForecast> {
    val timezoneOffset = city.timezone.toLong()
    return list
        .groupBy { item ->
            Instant.ofEpochSecond(item.dt)
                .atZone(ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(timezoneOffset.toInt())))
                .toLocalDate()
        }
        .entries
        .sortedBy { it.key }
        .take(5)
        .map { (date, items) ->
            val temps = items.map { it.main.temp }
            val midday = items.minByOrNull {
                kotlin.math.abs(
                    Instant.ofEpochSecond(it.dt)
                        .atZone(ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(timezoneOffset.toInt())))
                        .hour - 14
                )
            } ?: items.first()
            DailyForecast(
                timestamp = date.atStartOfDay(
                    ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(timezoneOffset.toInt()))
                ).toEpochSecond(),
                minTemp = temps.min(),
                maxTemp = temps.max(),
                iconCode = midday.weather.firstOrNull()?.icon ?: "",
                description = midday.weather.firstOrNull()?.description ?: "",
                humidity = items.map { it.main.humidity }.average().toInt(),
                windSpeed = items.map { it.wind.speed }.average()
            )
        }
}

fun ForecastResponseDto.filterHourlyForToday(): List<HourlyWeather> {
    val timezoneOffset = city.timezone.toLong()
    val zone = ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(timezoneOffset.toInt()))
    val today: LocalDate = Instant.now().atZone(zone).toLocalDate()
    return list
        .filter { item ->
            Instant.ofEpochSecond(item.dt).atZone(zone).toLocalDate() == today
        }
        .map { it.toHourlyWeather() }
}
