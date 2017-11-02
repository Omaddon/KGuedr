package com.ammyt.kguedr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

class ForecastActivity : AppCompatActivity(){

//    val TAG = ForecastActivity::class.java.canonicalName

    var forecast: Forecast? = null
        set(value) {
            // Access to views
            val forecastImage = findViewById<ImageView>(R.id.forecast_image)
            val maxTemp = findViewById<TextView>(R.id.max_temp)
            val minTemp = findViewById<TextView>(R.id.min_temp)
            val humidity = findViewById<TextView>(R.id.humidity)
            val description = findViewById<TextView>(R.id.forecast_description)

            // Model -> View
            if (value != null) {
                forecastImage.setImageResource(value.icon)
                description.text = value.description
                maxTemp.text = getString(R.string.max_temp_format, value.maxTemp)
                minTemp.text = getString(R.string.min_temp_format, value.minTemp)
                humidity.text = getString(R.string.humidity_format, value.humidity)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        // Mock model
        forecast = Forecast(
                25f,
                12f,
                42f,
                "Sunny",
                R.drawable.ico_01
        )
    }
}