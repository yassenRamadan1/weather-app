package com.example.weather_app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")

    class FavoriteDetails(val lan: Double, val lon: Double) :
        Screen("favoriteDetails/$lan/$lon") {
        companion object {
            const val routeWithArgs = "favoriteDetails/{lan}/{lon}"
            const val ARG_LAN = "lan"
            const val ARG_LON = "lon"
        }
    }
}