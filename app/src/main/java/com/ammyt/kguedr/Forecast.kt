package com.ammyt.kguedr

// Podemos hacer el constructor así, y acceder a los atributos directamente con ese nombre
// Con poner 'data' delante de la clase nos genera solo los getter y lo setter
data class Forecast(var maxTemp: Float,
               var minTemp: Float,
               var humidity: Float,
               var description: String,
               var icon: Int) {

    enum class TempUnit {
        CELSIUS,
        FAHRENHEIT
    }

    private fun toFahrenheit(celsius: Float) = celsius * 1.8f + 31

    fun getMaxTemp(units: TempUnit) = when (units) {
        TempUnit.CELSIUS        -> maxTemp
        TempUnit.FAHRENHEIT     -> toFahrenheit(maxTemp)
    }

    fun getMinTemp(units: TempUnit) = when (units) {
        TempUnit.CELSIUS        -> minTemp
        TempUnit.FAHRENHEIT     -> toFahrenheit(minTemp)
    }
}