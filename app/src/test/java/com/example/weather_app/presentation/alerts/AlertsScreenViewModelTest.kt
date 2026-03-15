package com.example.weather_app.presentation.alerts

import com.example.weather_app.MainDispatcherRule
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.usecases.FakeUserPreferencesRepository
import com.example.weather_app.domain.usecases.FakeWeatherRepository
import com.example.weather_app.domain.usecases.GetPreferredLocationUseCase
import com.example.weather_app.domain.usecases.alert.*
import com.example.weather_app.domain.usecases.alert.FakeAlarmScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AlertsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeWeatherRepo: FakeWeatherRepository
    private lateinit var fakeUserRepo: FakeUserPreferencesRepository
    private lateinit var fakeAlarmScheduler: FakeAlarmScheduler
    private lateinit var viewModel: AlertsScreenViewModel

    @Before
    fun setup() {
        fakeWeatherRepo = FakeWeatherRepository()
        fakeUserRepo = FakeUserPreferencesRepository()
        fakeAlarmScheduler = FakeAlarmScheduler()
        
        val getAllAlerts = GetAllAlertsUseCase(fakeWeatherRepo)
        val validateAlertUseCase = ValidateAlertUseCase()
        val addAlertUseCase = AddAlertUseCase(fakeWeatherRepo, validateAlertUseCase, fakeAlarmScheduler)
        val deleteAlertUseCase = DeleteAlertUseCase(fakeWeatherRepo, fakeAlarmScheduler)
        val setAlertActiveUseCase = SetAlertActiveUseCase(fakeWeatherRepo, fakeAlarmScheduler)
        val getPreferredLocationUseCase = GetPreferredLocationUseCase(fakeUserRepo, fakeUserRepo)

        viewModel = AlertsScreenViewModel(
            getAllAlerts,
            addAlertUseCase,
            deleteAlertUseCase,
            setAlertActiveUseCase,
            getPreferredLocationUseCase
        )
    }

    @Test
    fun observeAlerts_hasAlerts_uiStateSuccess() = runTest {
        // Given
        val alert = WeatherAlert(id = 1, startTimeMillis = 0, endTimeMillis = 0, alertType = AlertType.NOTIFICATION, conditionMode = AlertConditionMode.ANY, temperatureThreshold = null, windThreshold = null, cloudinessThreshold = null, isActive = true, isRepeated = false, lat = 0.0, lon = 0.0, cityName = "")
        fakeWeatherRepo.addAlert(alert)

        // When
        // ViewModel init automatically calls observeAlerts()

        // Then
        val state = viewModel.uiState.value
        assertThat(state is AlertsScreenUiState.Success, `is`(true))
        assertThat((state as AlertsScreenUiState.Success).alerts.size, `is`(1))
    }

    @Test
    fun onAlertTypeChanged_type_formStateUpdated() = runTest {
        // Given
        val type = AlertType.ALARM

        // When
        viewModel.onAlertTypeChanged(type)

        // Then
        assertThat(viewModel.formState.value.alertType, `is`(AlertType.ALARM))
    }

    @Test
    fun onRepeatChanged_repeated_formStateUpdated() = runTest {
        // Given
        val repeated = true

        // When
        viewModel.onRepeatChanged(repeated)

        // Then
        assertThat(viewModel.formState.value.isRepeated, `is`(true))
    }

    @Test
    fun deleteAlert_id_alertRemoved() = runTest {
        // Given
        val id = 1L
        fakeWeatherRepo.addAlert(WeatherAlert(id = id, startTimeMillis = 0, endTimeMillis = 0, alertType = AlertType.NOTIFICATION, conditionMode = AlertConditionMode.ANY, temperatureThreshold = null, windThreshold = null, cloudinessThreshold = null, isActive = true, lat = 0.0, lon = 0.0, cityName = ""))

        // When
        viewModel.deleteAlert(id)

        // Then
        assertThat(fakeWeatherRepo.alertsList.isEmpty(), `is`(true))
    }
}
