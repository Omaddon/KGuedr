package com.ammyt.kguedr.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.ammyt.kguedr.PREFERENCES_SHOW_CELSIUS
import com.ammyt.kguedr.R
import com.ammyt.kguedr.model.Forecast

class DetailActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_FORECAST = "EXTRA_FORECAST"
        private val EXTRA_DAY = "EXTRA_DAY"
        private val EXTRA_CITY = "EXTRA_CITY"

        fun intent(context: Context, city: String?, day: String?, forecast: Forecast): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_FORECAST, forecast)
            intent.putExtra(EXTRA_DAY, day)
            intent.putExtra(EXTRA_CITY, city)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.title = intent.getStringExtra(EXTRA_CITY)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // onOptionsItemSelected()

        val forecast = intent.getSerializableExtra(EXTRA_FORECAST) as? Forecast

        if (forecast != null) {
            // Actualizamos la interfaz
            val forecastImage = findViewById<ImageView>(R.id.forecast_image)
            val humidity = findViewById<TextView>(R.id.humidity)
            val description = findViewById<TextView>(R.id.forecast_description)
            val day = findViewById<TextView>(R.id.day)

            forecastImage.setImageResource(forecast.icon)
            description.text = forecast.description
            updateTemperature(forecast, temperatureUnits())
            humidity.text = getString(R.string.humidity_format, forecast.humidity)
            day.text = intent.getStringExtra(EXTRA_DAY)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            // Se ha pulsado el "back", volvemos a la actividad anterior
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateTemperature(forecast: Forecast, units: Forecast.TempUnit) {
        val unitsString = temperatureUnitsString(units)

        val maxTemp = findViewById<TextView>(R.id.max_temp)
        val minTemp = findViewById<TextView>(R.id.min_temp)

        maxTemp.text = getString(R.string.max_temp_format, forecast.getMaxTemp(units), unitsString)
        minTemp.text = getString(R.string.min_temp_format, forecast.getMinTemp(units), unitsString)
    }

    private fun temperatureUnitsString(units: Forecast.TempUnit) =
            if (units == Forecast.TempUnit.CELSIUS) "ºC" else "ºF"

    private fun temperatureUnits() =
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(PREFERENCES_SHOW_CELSIUS, true)) {
                Forecast.TempUnit.CELSIUS
            }
            else {
                Forecast.TempUnit.FAHRENHEIT
            }
}
