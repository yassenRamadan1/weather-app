package com.example.weather_app

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weather_app.designsystem.components.bottomnav.BlurBottomNavigationBar
import com.example.weather_app.navigation.Screen
import com.example.weather_app.navigation.WeatherNavGraph

private val bottomBarRoutes = setOf(
    Screen.Home.route,
    Screen.Favorites.route,
    Screen.Alerts.route,
    Screen.Settings.route,
)

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomBarRoutes

    val isDarkTheme = !isSystemInDarkTheme()

    StatusBarIconColor(
        darkIcons = isDarkTheme,
    )

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BlurBottomNavigationBar(navController = navController)
            }
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            WeatherNavGraph(navController = navController)
        }
    }
}


@Composable
fun StatusBarIconColor(darkIcons: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, view)
                .isAppearanceLightStatusBars = darkIcons
        }
    }
}

@Composable
fun StatusBarBackground(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .background(color),
    )
}

@Composable
fun StatusBar(
    backgroundColor: Color = Color.Transparent,
    darkIcons: Boolean = !isSystemInDarkTheme(),
) {
    StatusBarIconColor(darkIcons = darkIcons)
    StatusBarBackground(color = backgroundColor)
}