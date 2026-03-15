package com.example.weather_app.main

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.weather_app.domain.entity.user.AppTheme

fun Context.applyNativeTheme(theme: AppTheme) {
    val appCompatMode = when (theme) {
        AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(appCompatMode)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val uiMode = when (theme) {
            AppTheme.DARK -> UiModeManager.MODE_NIGHT_YES
            AppTheme.LIGHT -> UiModeManager.MODE_NIGHT_NO
            AppTheme.SYSTEM -> UiModeManager.MODE_NIGHT_AUTO
        }
        uiModeManager.setApplicationNightMode(uiMode)
    }
}