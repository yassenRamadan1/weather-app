package com.example.weather_app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")

    data class FavoriteDetails(val lat: Double, val lon: Double) : Screen(
        "favorite_details/$lat/$lon"
    ) {
        companion object {
            const val ARG_LAT = "lat"
            const val ARG_LON = "lon"
            const val routeWithArgs = "favorite_details/{$ARG_LAT}/{$ARG_LON}"
        }
    }
}