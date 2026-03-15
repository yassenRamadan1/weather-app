package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.domain.usecases.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AlertUseCaseTest {

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var fakeAlarmScheduler: FakeAlarmScheduler
    private lateinit var validateAlertUseCase: ValidateAlertUseCase
    private lateinit var addAlertUseCase: AddAlertUseCase
    private lateinit var deleteAlertUseCase: DeleteAlertUseCase
    private lateinit var getAllAlertsUseCase: GetAllAlertsUseCase
    private lateinit var setAlertActiveUseCase: SetAlertActiveUseCase

    @Before
    fun setup() {
        fakeRepository = FakeWeatherRepository()
        fakeAlarmScheduler = FakeAlarmScheduler()
        validateAlertUseCase = ValidateAlertUseCase()
        addAlertUseCase = AddAlertUseCase(fakeRepository, validateAlertUseCase, fakeAlarmScheduler)
        deleteAlertUseCase = DeleteAlertUseCase(fakeRepository, fakeAlarmScheduler)
        getAllAlertsUseCase = GetAllAlertsUseCase(fakeRepository)
        setAlertActiveUseCase = SetAlertActiveUseCase(fakeRepository, fakeAlarmScheduler)
    }

    @Test
    fun addAlertUseCase_validAlert_alertAddedAndScheduled() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val alert = WeatherAlert(
            startTimeMillis = now + 60_000,
            endTimeMillis = now + 120_000,
            alertType = AlertType.NOTIFICATION,
            conditionMode = AlertConditionMode.ANY,
            temperatureThreshold = null,
            windThreshold = null,
            cloudinessThreshold = null,
            isActive = true,
            isRepeated = false,
            lat = 30.0,
            lon = 31.0,
            cityName = "Cairo"
        )

        // When
        val id = addAlertUseCase(alert)

        // Then
        assertThat(fakeRepository.alertsList.size, `is`(1))
        assertThat(fakeAlarmScheduler.scheduledAlerts.containsKey(id), `is`(true))
    }

    @Test
    fun deleteAlertUseCase_id_alertRemovedAndCancelled() = runTest {
        // Given
        val id = 1L
        fakeRepository.addAlert(WeatherAlert(id = id, startTimeMillis = 0, endTimeMillis = 0, alertType = AlertType.NOTIFICATION, conditionMode = AlertConditionMode.ANY, temperatureThreshold = null, windThreshold = null, cloudinessThreshold = null, isActive = true, isRepeated = false, lat = 0.0, lon = 0.0, cityName = ""))

        // When
        deleteAlertUseCase(id)

        // Then
        assertThat(fakeRepository.alertsList.isEmpty(), `is`(true))
        assertThat(fakeAlarmScheduler.cancelledAlerts.contains(id), `is`(true))
    }

    @Test
    fun getAllAlertsUseCase_noInput_returnsAlertFlow() = runTest {
        // Given
        fakeRepository.addAlert(WeatherAlert(id = 1, startTimeMillis = 0, endTimeMillis = 0, alertType = AlertType.NOTIFICATION, conditionMode = AlertConditionMode.ANY, temperatureThreshold = null, windThreshold = null, cloudinessThreshold = null, isActive = true, isRepeated = false, lat = 0.0, lon = 0.0, cityName = ""))

        // When
        val result = getAllAlertsUseCase().first()

        // Then
        assertThat(result.size, `is`(1))
        assertThat(result[0].id, `is`(1L))
    }

    @Test
    fun setAlertActiveUseCase_idAndStatus_updatesStatusAndSchedule() = runTest {
        // Given
        val id = 1L
        val now = System.currentTimeMillis()
        fakeRepository.addAlert(WeatherAlert(id = id, startTimeMillis = now + 60_000, endTimeMillis = now + 120_000, alertType = AlertType.NOTIFICATION, conditionMode = AlertConditionMode.ANY, temperatureThreshold = null, windThreshold = null, cloudinessThreshold = null, isActive = false, isRepeated = false, lat = 0.0, lon = 0.0, cityName = ""))

        // When
        setAlertActiveUseCase(id, true)

        // Then
        assertThat(fakeRepository.alertsList[0].isActive, `is`(true))
        assertThat(fakeAlarmScheduler.scheduledAlerts.containsKey(id), `is`(true))
    }

    @Test(expected = AppError.PeriodOrder::class)
    fun validateAlertUseCase_invalidPeriod_throwsException() {
        // Given
        val now = System.currentTimeMillis()
        val alert = WeatherAlert(
            startTimeMillis = now + 120_000,
            endTimeMillis = now + 60_000, // End before start
            alertType = AlertType.NOTIFICATION,
            conditionMode = AlertConditionMode.ANY,
            temperatureThreshold = null,
            windThreshold = null,
            cloudinessThreshold = null,
            isActive = true,
            isRepeated = false,
            lat = 30.0,
            lon = 31.0,
            cityName = "Cairo"
        )

        // When
        validateAlertUseCase(alert)

        // Then exception is thrown
    }
}
