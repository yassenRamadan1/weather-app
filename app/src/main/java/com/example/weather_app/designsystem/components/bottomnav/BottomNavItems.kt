package com.example.weather_app.designsystem.components.bottomnav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.weather_app.R
import com.example.weather_app.navigation.Screen

data class BottomNavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val label: Int,
)


val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = R.string.nav_home,
    ),
    BottomNavItem(
        screen = Screen.Favorites,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        label = R.string.nav_favorites,
    ),
    BottomNavItem(
        screen = Screen.Alerts,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        label = R.string.nav_alert,
    ),
    BottomNavItem(
        screen = Screen.Settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        label = R.string.nav_settings,
    ),
)
