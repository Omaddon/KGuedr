package com.ammyt.kguedr.model

import java.io.Serializable


data class City(
        var name: String,
        var forecast: Forecast) : Serializable