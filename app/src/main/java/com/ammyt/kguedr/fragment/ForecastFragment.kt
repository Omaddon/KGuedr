package com.ammyt.kguedr.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewSwitcher
import com.ammyt.kguedr.CONSTANT_APIKEY
import com.ammyt.kguedr.PREFERENCES_SHOW_CELSIUS
import com.ammyt.kguedr.R
import com.ammyt.kguedr.activity.SettingsActivity
import com.ammyt.kguedr.model.City
import com.ammyt.kguedr.model.Forecast
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.net.URL
import java.util.*


class ForecastFragment : Fragment() {

    // Enumerado para la selección de la vista a mostrar en el Switch
    enum class VIEW_INDEX(val index: Int) {
        LOADING(0),
        FORECAST(1)
    }

    // Es un singletone con atributos y métodos estáticos ~ static de java
    companion object {
        val REQUEST_UNITS = 1
        private val ARG_CITY = "ARG_CITY"

        fun newInstance(city: City): ForecastFragment {
            val fragment = ForecastFragment()

            val arguments = Bundle()
            arguments.putSerializable(ARG_CITY, city)
            fragment.arguments = arguments

            return fragment
        }
    }

    lateinit var root: View
    lateinit var maxTemp: TextView
    lateinit var minTemp: TextView
    lateinit var viewSwitcher: ViewSwitcher

    var city: City? = null
        set(value) {
            field = value
            value?.let {
                root.findViewById<TextView>(R.id.city).text = value.name
                forecast = value.forecast
            }
        }

    var forecast: Forecast? = null
        set(value) {
            // "field" es el lugar donde realmente se guarda el value. De esta forma le asignamos
            // el valor "value" a "forecast" ANTES de hacer el resto
            field = value
            // Access to views
            val forecastImage = root.findViewById<ImageView>(R.id.forecast_image)
            maxTemp = root.findViewById(R.id.max_temp)
            minTemp = root.findViewById(R.id.min_temp)
            val humidity = root.findViewById<TextView>(R.id.humidity)
            val description = root.findViewById<TextView>(R.id.forecast_description)

            // Model -> View
//            if (value != null) {
            if (value != null) {
                forecastImage.setImageResource(value.icon)
                description.text = value.description
                updateTemperature()
                humidity.text = getString(R.string.humidity_format, value.humidity)

                // Ya tenemos forecast descargado, así que lo mostramos
                viewSwitcher.displayedChild = VIEW_INDEX.FORECAST.index

                // Debemos cachear los datos para que no se los descargue cada vez
                city?.forecast = value
            }
            else {
                updateForecast()
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
            viewSwitcher = root.findViewById(R.id.view_switcher)
            viewSwitcher.setInAnimation(activity, android.R.anim.fade_in)
            viewSwitcher.setOutAnimation(activity, android.R.anim.fade_out)

            if (arguments != null) {
                city = arguments.getSerializable(ARG_CITY) as? City
            }
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

    // ~ viewWillAppear
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && forecast != null) {
            updateTemperature()
        }
    }

    private fun updateForecast() {

        // Le indicamos al ViewSwitcher qué vista debe mostrar. 0 = progressBar | 1 = forecast
        viewSwitcher.displayedChild = VIEW_INDEX.LOADING.index

        // OJO!! Debemos realizar toda la descarga en segundo plano!!
        // Recordemos que no podemos tocar la interfaz en segundo plano

        // Hay que importar la librería experimental de "anko"
        async(UI) {
            val newForecast: Deferred<Forecast?> = bg {
                downloadForecast(city)
            }

            val downloadedForecast = newForecast.await()

            if (downloadedForecast != null) {
                // Descarga correcta
                forecast = downloadedForecast
            }
            else {
                // Error en la descarga
                AlertDialog.Builder(activity)
                        .setTitle("Error")
                        .setMessage("Error downloading forecast information.")
                        .setPositiveButton("Retry", { dialog, _ ->
                            dialog.dismiss()
                            updateForecast()
                        })
                        // Deberíamos informar a la actividad en lugar de hacer esto
                        .setNegativeButton("Exit", { _, _ -> activity.finish() })
                        .show()
            }
        }

// ------------------------------------------------------------------------------------------
        // CUIDADO!! Se crea una dependecia con la Actvidad. De tal forma que si se gira el
        // dispositivo y se recrea una nueva actividad, al intentar llamar a onPostExecute se
        // hará referencia a la vieja actividad destruída, produciendo una Excepción y crash.
//        val weatherDownloader = object : AsyncTask<City, Int, Forecast?>() {
//
//            override fun onPreExecute() {
//                super.onPreExecute()
//                // Esto se ejecuta en el hilo principal
//            }
//
//            override fun doInBackground(vararg params: City): Forecast? {
//                return downloadForecast(params[0])
//            }
//
//            override fun onPostExecute(result: Forecast?) {
//                super.onPostExecute(result)
//                // Esto se ejecuta en el hilo principal
//                if (result != null) {
//                    // Eso quiere decir que NO ha habido errores -> actualizamos interfaz
//                    city?.forecast = result
//                    forecast = result
//                }
//            }
//        }

//        weatherDownloader.execute(city)
// ------------------------------------------------------------------------------------------

    }

    private fun downloadForecast(city: City?): Forecast? {
        try {
            // Nos descargamos la información del tiempo (sin frameworks de apoyo)
            val url = URL("https://api.openweathermap.org/data/2.5/forecast/daily?q=${city?.name}&lang=sp&units=metric&appid=${CONSTANT_APIKEY}")

            // Esto es equivalente a lo de abajo comentado
            val jsonString = Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next()

            /*
            val con = url.openConnection() as HttpURLConnection
            con.connect()

            val data = ByteArray(1024)
            var downloadBytes: Int
            val input = con.inputStream
            val sb = StringBuilder()

            downloadBytes = input.read(data)
            while (downloadBytes != -1) {
                sb.append(String(data, 0, downloadBytes))
                downloadBytes = input.read(data)
            }
            */

            // Analizamos los datos descargados
            //val jsonRoot = JSONObject(sb.toString())
            val jsonRoot = JSONObject(jsonString)
            val list = jsonRoot.getJSONArray("list")
            val today = list.getJSONObject(0)
            val jsonMaxTemp = today.getJSONObject("temp").getDouble("max").toFloat()
            val jsonMinTemp = today.getJSONObject("temp").getDouble("min").toFloat()
            val jsonHumidity = today.getDouble("humidity").toFloat()
            val jsonDescription = today.getJSONArray("weather").getJSONObject(0).getString("description")
            var jsonIconString = today.getJSONArray("weather").getJSONObject(0).getString("icon")

            // Convertimos el iconString a un Drawble
            jsonIconString = jsonIconString.substring(0, jsonIconString.length - 1)
            val iconInt = jsonIconString.toInt()
            val iconResource = when (iconInt) {
                2 -> R.drawable.ico_02
                3 -> R.drawable.ico_03
                4 -> R.drawable.ico_04
                9 -> R.drawable.ico_09
                10 -> R.drawable.ico_10
                11 -> R.drawable.ico_11
                13 -> R.drawable.ico_13
                50 -> R.drawable.ico_50
                else -> R.drawable.ico_01
            }

            // Para pruebas de simulación de retardo en descarga y testeo de problemas
            Thread.sleep(1000)

            // Una vez que tenemos todos los datos, creamos nuestro Forecast
            return Forecast(
                    jsonMaxTemp,
                    jsonMinTemp,
                    jsonHumidity,
                    jsonDescription,
                    iconResource
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
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