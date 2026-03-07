package com.example.weather_app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.datasource.UserPreferencesDataSource
import com.example.weather_app.domain.entity.UserPreferences
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.domain.error.toUiMessage
import com.example.weather_app.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepo: UserPreferencesDataSource,
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = userRepo.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getWeatherUseCase(lat, lon).collect { result ->
                result.fold(
                    onSuccess = { _uiState.value = HomeUiState.Success(it) },
                    onFailure = { error ->
                        val appError = error as? AppError ?: AppError.UnknownError()
                        _uiState.value = HomeUiState.Error(appError.toUiMessage())
                    }
                )
            }
        }
    }

    fun onLocationPermissionDenied() {
        _uiState.value = HomeUiState.LocationPermissionRequired
    }
}