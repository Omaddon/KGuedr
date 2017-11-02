package com.ammyt.kguedr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity(){

    val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mock model
        val forecast = Forecast(
                25f,
                12f,
                42f,
                "Sunny",
                R.drawable.ico_01
        )

        setForecast(forecast)
    }

    private fun setForecast(forecast: Forecast) {

        // Access to views
        val forecastImage = findViewById<ImageView>(R.id.forecast_image)
        val maxTemp = findViewById<TextView>(R.id.max_temp)
        val minTemp = findViewById<TextView>(R.id.min_temp)
        val humidity = findViewById<TextView>(R.id.humidity)
        val description = findViewById<TextView>(R.id.forecast_description)

        // Model -> View
        forecastImage.setImageResource(forecast.icon)
        description.text = forecast.description
        maxTemp.text = getString(R.string.max_temp_format, forecast.maxTemp)
        minTemp.text = getString(R.string.min_temp_format, forecast.minTemp)
        humidity.text = getString(R.string.humidity_format, forecast.humidity)
    }
}