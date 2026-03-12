package com.example.weather_app.data.weather

import com.example.weather_app.data.weather.local.entity.WeatherEntity
import com.example.weather_app.data.weather.remote.dto.CloudsDto
import com.example.weather_app.data.weather.remote.dto.CoordDto
import com.example.weather_app.data.weather.remote.dto.CurrentWeatherDto
import com.example.weather_app.data.weather.remote.dto.MainDto
import com.example.weather_app.data.weather.remote.dto.SysDto
import com.example.weather_app.data.weather.remote.dto.WeatherDescDto
import com.example.weather_app.data.weather.remote.dto.WindDto
import com.example.weather_app.domain.entity.Weather

import com.example.weather_app.data.weather.local.entity.FavoriteLocationEntity
import com.example.weather_app.domain.entity.FavoriteLocation

fun FavoriteLocationEntity.toDomain(): FavoriteLocation = FavoriteLocation(
    cityName = cityName,
    countryCode = countryCode,
    lat = lat,
    lon = lon
)

fun FavoriteLocation.toEntity(): FavoriteLocationEntity = FavoriteLocationEntity(
    cityName = cityName,
    countryCode = countryCode,
    lat = lat,
    lon = lon
)

fun CurrentWeatherDto.toDomain(): Weather = Weather(
    cityName = name,
    countryCode = sys.country,
    temperature = main.temp,
    feelsLike = main.feelsLike,
    description = weather.firstOrNull()?.description ?: "",
    iconCode = weather.firstOrNull()?.icon ?: "",
    humidity = main.humidity,
    windSpeed = wind.speed,
    pressure = main.pressure,
    cloudiness = clouds.all,
    visibility = visibility,
    timestamp = dt,
    lat = coord.lat,
    lon = coord.lon
)
fun CurrentWeatherDto.toEntity(): WeatherEntity = WeatherEntity(
    id = 1,
    cityName = name,
    countryCode = sys.country,
    temperature = main.temp,
    feelsLike = main.feelsLike,
    description = weather.firstOrNull()?.description ?: "",
    iconCode = weather.firstOrNull()?.icon ?: "",
    humidity = main.humidity,
    windSpeed = wind.speed,
    pressure = main.pressure,
    cloudiness = clouds.all,
    visibility = visibility,
    timestamp = dt,
    lat = coord.lat,
    lon = coord.lon
)

fun Weather.toEntity(): WeatherEntity = WeatherEntity(
    id = 1,
    cityName = cityName,
    countryCode = countryCode,
    temperature = temperature,
    feelsLike = feelsLike,
    description = description,
    iconCode = iconCode,
    humidity = humidity,
    windSpeed = windSpeed,
    pressure = pressure,
    cloudiness = cloudiness,
    visibility = visibility,
    timestamp = timestamp,
    lat = lat,
    lon = lon
)

fun WeatherEntity.toDomain(): Weather = Weather(
    cityName = cityName,
    countryCode = countryCode ?: "",
    temperature = temperature,
    feelsLike = feelsLike,
    description = description,
    iconCode = iconCode,
    humidity = humidity,
    windSpeed = windSpeed,
    pressure = pressure,
    cloudiness = cloudiness,
    visibility = visibility,
    timestamp = timestamp,
    lat = lat,
    lon = lon
)
fun WeatherEntity.toDto(): CurrentWeatherDto = CurrentWeatherDto(
    name = cityName,
    coord = CoordDto(lat, lon),
    main = MainDto(temperature, feelsLike, humidity, pressure),
    weather = listOf(WeatherDescDto(description, iconCode)),
    wind = WindDto(windSpeed),
    clouds = CloudsDto(cloudiness),
    visibility = visibility,
    dt = timestamp,
    sys = SysDto(countryCode?:"")
)
