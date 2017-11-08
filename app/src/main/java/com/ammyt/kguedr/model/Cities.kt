package com.ammyt.kguedr.model

import com.ammyt.kguedr.R
import java.io.Serializable


// En lugar de poner "class", ponemos "object" y así lo convertimos en un Singletone
object Cities : Serializable {
    // "listOf" es inmutable. Con "mutableListOf" la hacemos mutable. De momento mockeamos la lista.
    private var cities: List<City> = listOf(
            City("Madrid", Forecast(25f, 12f, 35f, "Sunny", R.drawable.ico_01)),
            City("Jaén", Forecast(25f, 23f, 12f, "Cloudy", R.drawable.ico_02)),
            City("Tárrega", Forecast(20f, 2f, 42f, "Sunny", R.drawable.ico_10))
    )

    val count: Int
        get() = cities.size

//    fun getCity(index: Int) = cities[index]

    // Con "operator" sobreescribimos algunos operadores.
    operator fun get(i: Int) = cities[i]

    fun toArray() = cities.toTypedArray()
}