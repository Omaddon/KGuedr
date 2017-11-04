package com.ammyt.kguedr

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class ForecastUnitTest {

    lateinit var forecast: Forecast

    @Before
    fun setUp() {
        forecast = Forecast(
                25f,
                10f,
                35f,
                "Cloudy",
                R.drawable.ico_01)
    }

    @Test
    fun maxTempUnitsConversion() {
        assertEquals(77f, forecast.getMaxTemp(Forecast.TempUnit.FAHRENHEIT))
    }

    @Test
    fun minTempUnitsConversion() {
        assertEquals(50f, forecast.getMinTemp(Forecast.TempUnit.FAHRENHEIT))
    }

    @Test
    fun maxTempUnitsInCelsius() {
        assertEquals(25f, forecast.getMaxTemp(Forecast.TempUnit.CELSIUS))
    }

    @Test
    fun minTempUnitsInCelsius() {
        assertEquals(10f, forecast.getMinTemp(Forecast.TempUnit.CELSIUS))
    }

    @Test(expected = IllegalArgumentException::class)
    fun humidityOverRange() {
        Forecast(
                25f,
                10f,
                100.01f,
                "Cloudy",
                R.drawable.ico_01
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun humidityUnderRange() {
        Forecast(
                25f,
                10f,
                -1f,
                "Cloudy",
                R.drawable.ico_01
        )
    }
}