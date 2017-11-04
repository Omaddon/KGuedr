package com.ammyt.kguedr.fragment

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.ammyt.kguedr.model.Forecast
import com.ammyt.kguedr.PREFERENCES_SHOW_CELSIUS
import com.ammyt.kguedr.R
import com.ammyt.kguedr.activity.SettingsActivity


class ForecastFragment : Fragment() {

    // Es un singletone con atributos y métodos estáticos ~ static de java
    companion object {
        val REQUEST_UNITS = 1
    }

    lateinit var root: View
    lateinit var maxTemp: TextView
    lateinit var minTemp: TextView

    var forecast: Forecast? = null
        set(value) {
            // Es el lugar donde realmente se guarda el value
            field = value
            // Access to views
            val forecastImage = root.findViewById<ImageView>(R.id.forecast_image)
            maxTemp = root.findViewById(R.id.max_temp)
            minTemp = root.findViewById(R.id.min_temp)
            val humidity = root.findViewById<TextView>(R.id.humidity)
            val description = root.findViewById<TextView>(R.id.forecast_description)

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

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        inflater.let {
            // "it" es el valor del inflater cuando no es null (inflater.let)
            root = it!!.inflate(R.layout.fragment_forecast, container, false)

            // Mock model
            forecast = Forecast(
                    32f,
                    22f,
                    42f,
                    "Sunny",
                    R.drawable.ico_01
            )
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_show_settings) {
            val units = if (temperatureUnits() == Forecast.TempUnit.CELSIUS)
                R.id.celsius_rb
            else
                R.id.farenheit_rb

            val intent = SettingsActivity.intent(activity, units)

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

                PreferenceManager.getDefaultSharedPreferences(activity)
                        .edit()
                        .putBoolean(PREFERENCES_SHOW_CELSIUS, unitsSelected == R.id.celsius_rb)
                        .apply()

                updateTemperature()

                // Para usar la skackBar, debemos añadir la dependencia "design"
                Snackbar.make(root,"Preferences changed.",
                        Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            PreferenceManager.getDefaultSharedPreferences(activity)
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
            if (PreferenceManager.getDefaultSharedPreferences(activity)
                    .getBoolean(PREFERENCES_SHOW_CELSIUS, true)) {
                Forecast.TempUnit.CELSIUS
            }
            else {
                Forecast.TempUnit.FAHRENHEIT
            }
}