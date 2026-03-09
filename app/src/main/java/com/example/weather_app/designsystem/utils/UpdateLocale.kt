package com.example.weather_app.designsystem.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.updateLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    val localizedContext = createConfigurationContext(config)
    return object : android.content.ContextWrapper(this) {
        override fun getResources(): android.content.res.Resources = localizedContext.resources
        override fun getAssets(): android.content.res.AssetManager = localizedContext.assets
        override fun getSystemService(name: String): Any? = localizedContext.getSystemService(name)
        override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
            return localizedContext.createConfigurationContext(overrideConfiguration)
        }
    }
}