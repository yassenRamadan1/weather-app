package com.example.weather_app.presentation.components.models

data class PickedLocation(
    val lat: Double,
    val lon: Double,
    val countryCode: String,
    val cityName: String?
)
data class AddressDetails(
    val city: String,
    val countryCode: String
)