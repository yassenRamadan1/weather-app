package com.example.weather_app.data.weather

import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import com.example.weather_app.data.weather.remote.dto.ForecastItemDto
import com.example.weather_app.data.weather.remote.dto.ForecastResponseDto
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset.ofTotalSeconds

fun ForecastItemDto.toHourlyWeather(): HourlyWeather = HourlyWeather(
    timestamp = dt,
    temperature = main.temp,
    weatherStateId = weather.firstOrNull()?.id ?: 800,
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
                .atZone(ZoneId.ofOffset("UTC", ofTotalSeconds(timezoneOffset.toInt())))
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
                        .atZone(ZoneId.ofOffset("UTC", ofTotalSeconds(timezoneOffset.toInt())))
                        .hour - 14
                )
            } ?: items.first()
            DailyForecast(
                timestamp = date.atStartOfDay(
                    ZoneId.ofOffset("UTC", ofTotalSeconds(timezoneOffset.toInt()))
                ).toEpochSecond(),
                minTemp = temps.min(),
                maxTemp = temps.max(),
                weatherStateId = midday.weather.firstOrNull()?.id ?: 800,
                iconCode = midday.weather.firstOrNull()?.icon ?: "",
                description = midday.weather.firstOrNull()?.description ?: "",
                humidity = items.map { it.main.humidity }.average().toInt(),
                windSpeed = items.map { it.wind.speed }.average()
            )
        }
}

fun ForecastResponseDto.filterHourlyForToday(): List<HourlyWeather> {
    val timezoneOffset = city.timezone.toLong()
    val zone = ZoneId.ofOffset("UTC", ofTotalSeconds(timezoneOffset.toInt()))
    val today: LocalDate = Instant.now().atZone(zone).toLocalDate()
    return list
        .filter { item ->
            Instant.ofEpochSecond(item.dt).atZone(zone).toLocalDate() == today
        }
        .map { it.toHourlyWeather() }
}

fun HourlyWeather.toEntity(lat: Double, lon: Double) = HourlyWeatherEntity(
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    temperature = temperature,
    weatherStateId = weatherStateId,
    iconCode = iconCode,
    description = description,
    windSpeed = windSpeed,
    humidity = humidity,
    pop = pop
)

fun HourlyWeatherEntity.toDomain() = HourlyWeather(
    timestamp = timestamp,
    temperature = temperature,
    weatherStateId = weatherStateId,
    iconCode = iconCode,
    description = description,
    windSpeed = windSpeed,
    humidity = humidity,
    pop = pop
)

fun DailyForecast.toEntity(lat: Double, lon: Double) = DailyForecastEntity(
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    minTemp = minTemp,
    maxTemp = maxTemp,
    weatherStateId = weatherStateId,
    iconCode = iconCode,
    description = description,
    humidity = humidity,
    windSpeed = windSpeed
)

fun DailyForecastEntity.toDomain() = DailyForecast(
    timestamp = timestamp,
    minTemp = minTemp,
    maxTemp = maxTemp,
    weatherStateId = weatherStateId,
    iconCode = iconCode,
    description = description,
    humidity = humidity,
    windSpeed = windSpeed
)
