package com.example.weather_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.weather_app.presentation.alerts.AlertScreen
import com.example.weather_app.presentation.favorites.FavoritesScreen
import com.example.weather_app.presentation.favorites.favoritedetails.FavoriteDetailsScreen
import com.example.weather_app.presentation.home.HomeScreen
import com.example.weather_app.presentation.settings.SettingsScreen


@Composable
fun WeatherNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ){
        composable(route = Screen.Home.route) {
            HomeScreen(
            )
        }

        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                onClickFavorite = { lat,lon ->
                    navController.navigate(Screen.FavoriteDetails(lat,lon).route)
                },
            )
        }


        composable(
            route = Screen.FavoriteDetails.routeWithArgs,
            arguments = listOf(
                navArgument(Screen.FavoriteDetails.ARG_LAT) {
                    type = NavType.StringType
                },
                navArgument(Screen.FavoriteDetails.ARG_LON) {
                    type = NavType.StringType
                }
            ),
        ) {

            FavoriteDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Alerts.route) {
            AlertScreen()
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
fun NavHostController.navigateToBottomNavDestination(screen: Screen) {
    navigate(screen.route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}