package com.ammyt.kguedr.model

import java.io.Serializable


data class City(
        var name: String,
        var forecast: Forecast) : Serializable {

    // Lo sobreescribimos para mostrar lo que queramos en la celda de nuestra tabla
    override fun toString() = name
}