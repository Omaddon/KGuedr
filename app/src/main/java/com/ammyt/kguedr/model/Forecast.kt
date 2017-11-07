package com.ammyt.kguedr.model

import java.io.Serializable

// Podemos hacer el constructor así, y acceder a los atributos directamente con ese nombre
// Con poner 'data' delante de la clase nos genera solo los getter y lo setter
data class Forecast(
        var maxTemp: Float,
        var minTemp: Float,
        var humidity: Float,
        var description: String,
        var icon: Int) : Serializable {

    enum class TempUnit {
        CELSIUS,
        FAHRENHEIT
    }

    private fun toFahrenheit(celsius: Float) = celsius * 1.8f + 32

    init {
        if (humidity !in 0f..100f) {
            throw IllegalArgumentException("Humidity should be between 0 and 100.")
        }
    }

    fun getMaxTemp(units: TempUnit) = when (units) {
        TempUnit.CELSIUS        -> maxTemp
        TempUnit.FAHRENHEIT     -> toFahrenheit(maxTemp)
    }

    fun getMinTemp(units: TempUnit) = when (units) {
        TempUnit.CELSIUS        -> minTemp
        TempUnit.FAHRENHEIT     -> toFahrenheit(minTemp)
    }
}