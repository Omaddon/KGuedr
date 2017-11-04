package com.ammyt.kguedr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ForecastActivity : AppCompatActivity(){

//    val TAG = ForecastActivity::class.java.canonicalName

    // Es un singletone con atributos y métodos estáticos ~ static de java
    companion object {
        val REQUEST_UNITS = 1
    }

    lateinit var maxTemp: TextView
    lateinit var minTemp: TextView

    var forecast: Forecast? = null
        set(value) {
            // Es el lugar donde realmente se guarda el value
            field = value
            // Access to views
            val forecastImage = findViewById<ImageView>(R.id.forecast_image)
            maxTemp = findViewById(R.id.max_temp)
            minTemp = findViewById(R.id.min_temp)
            val humidity = findViewById<TextView>(R.id.humidity)
            val description = findViewById<TextView>(R.id.forecast_description)

            // Model -> View
//            if (value != null) {
            value?.let {
                forecastImage.setImageResource(value.icon)
                description.text = value.description
                updateTemperature()
                humidity.text = getString(R.string.humidity_format, value.humidity)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        // Mock model
        forecast = Forecast(
                32f,
                22f,
                42f,
                "Sunny",
                R.drawable.ico_01
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.settings, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_show_settings) {
            val units = if (temperatureUnits() == Forecast.TempUnit.CELSIUS)
                R.id.celsius_rb
            else
                R.id.farenheit_rb

            val intent = SettingsActivity.intent(this, units)

            //startActivity(intent)
            startActivityForResult(intent, REQUEST_UNITS)

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_UNITS) {
            if (resultCode == Activity.RESULT_OK) {
                // Han pulsado OK
                val unitsSelected = data?.getIntExtra(SettingsActivity.EXTRA_UNITS, R.id.celsius_rb)
                when (unitsSelected) {
                    R.id.celsius_rb -> {
//                        Toast.makeText(this, "Celsius selected.", Toast.LENGTH_LONG)
//                                .show()
                    }
                    R.id.farenheit_rb -> {

                    }
                }

                val oldShowCelsius = temperatureUnits()

                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean(PREFERENCES_SHOW_CELSIUS, unitsSelected == R.id.celsius_rb)
                        .apply()

                updateTemperature()

                // Para usar la skackBar, debemos añadir la dependencia "design"
                Snackbar.make(findViewById<View>(android.R.id.content),
                        "Preferences changed.",
                        Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            PreferenceManager.getDefaultSharedPreferences(this)
                                    .edit()
                                    .putBoolean(PREFERENCES_SHOW_CELSIUS,
                                            oldShowCelsius == Forecast.TempUnit.CELSIUS)
                                    .apply()

                            updateTemperature()
                        }
                        .show()
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                // Han pulsado CANCEL

            }
        }
    }

    private fun updateTemperature() {
        val units = temperatureUnits()
        val unitsString = temperatureUnitsString(units)

        maxTemp.text = getString(R.string.max_temp_format, forecast?.getMaxTemp(units), unitsString)
        minTemp.text = getString(R.string.min_temp_format, forecast?.getMinTemp(units), unitsString)
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