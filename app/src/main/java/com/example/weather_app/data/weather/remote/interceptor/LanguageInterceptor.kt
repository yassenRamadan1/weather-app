package com.example.weather_app.data.weather.remote.interceptor

import com.example.weather_app.data.user.UserPreferencesDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class LanguageInterceptor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val lang = runBlocking {
            userPreferencesDataSource.userPreferences.first().language.code
        }
        val url = chain.request().url.newBuilder()
            .addQueryParameter("lang", lang)
            .build()
        return chain.proceed(chain.request().newBuilder().url(url).build())
    }
}