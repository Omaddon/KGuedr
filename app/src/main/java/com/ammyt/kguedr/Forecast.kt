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
}