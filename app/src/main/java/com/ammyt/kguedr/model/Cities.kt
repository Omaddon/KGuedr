package com.ammyt.kguedr.model

import java.io.Serializable


// En lugar de poner "class", ponemos "object" y así lo convertimos en un Singletone
object Cities : Serializable {
    // "listOf" es inmutable. Con "mutableListOf" la hacemos mutable. De momento mockeamos la lista.
    private var cities: List<City> = listOf(
            City("Madrid"),
            City("Chicago"),
            City("Tokyo")
    )

    val count: Int
        get() = cities.size

//    fun getCity(index: Int) = cities[index]

    // Con "operator" sobreescribimos algunos operadores.
    operator fun get(i: Int) = cities[i]

    fun toArray() = cities.toTypedArray()
}