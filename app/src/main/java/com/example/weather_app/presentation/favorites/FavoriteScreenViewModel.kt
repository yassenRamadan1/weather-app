package com.example.weather_app.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.entity.weather.FavoriteLocation
import com.example.weather_app.domain.usecases.AddFavoriteLocationUseCase
import com.example.weather_app.domain.usecases.DeleteFavoriteLocationUseCase
import com.example.weather_app.domain.usecases.GetFavoriteLocationsUseCase
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.presentation.uierror.UiText
import com.example.weather_app.presentation.uierror.toUiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FavoriteScreenViewModel(
    private val getFavoriteLocations: GetFavoriteLocationsUseCase,
    private val deleteFavoriteLocation: DeleteFavoriteLocationUseCase,
    private val addFavoriteLocationUseCase: AddFavoriteLocationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoritesScreenUiState>(FavoritesScreenUiState.Empty)
    val uiState: StateFlow<FavoritesScreenUiState> = _uiState
    private val _effect = MutableSharedFlow<FavoritesScreenEffect>()
    val effect: SharedFlow<FavoritesScreenEffect> = _effect.asSharedFlow()


    init {
        loadFavoriteLocations()
    }

    fun loadFavoriteLocations() {
        viewModelScope.launch {
            try {
                getFavoriteLocations().collect { locations ->
                    if (locations.isEmpty()) {
                        _uiState.value = FavoritesScreenUiState.Empty
                    } else {
                        _uiState.value = FavoritesScreenUiState.Success(locations)
                    }
                }
            } catch (e: Exception) {
                val appError = e as? AppError ?: AppError.UnknownError()
                _uiState.value = FavoritesScreenUiState.Error(
                    message = appError.toUiText()
                )
                _effect.emit(
                    FavoritesScreenEffect.ShowMessage(
                        message = appError.toUiText()
                    )
                )
            }

        }
    }

    fun addFavoriteLocation(lat: Double, lon: Double, name: String, countryCode: String) {
        val location = FavoriteLocation(
            cityName = name,
            countryCode = countryCode,
            lat = lat,
            lon = lon
        )
        viewModelScope.launch {
            try {
                addFavoriteLocationUseCase(location)
            } catch (e: Exception) {
                val appError = e as? AppError ?: AppError.UnknownError()
                _uiState.value = FavoritesScreenUiState.Error(
                    message = appError.toUiText()
                )

            }
        }
    }

    fun removeFavoriteLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                deleteFavoriteLocation(lat, lon)
            } catch (e: Exception) {
                val appError = e as? AppError ?: AppError.UnknownError()
                _uiState.value = FavoritesScreenUiState.Error(
                    message = appError.toUiText()
                )

            }
        }
    }
}