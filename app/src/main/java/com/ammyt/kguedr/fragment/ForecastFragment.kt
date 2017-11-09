package com.ammyt.kguedr.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.ViewSwitcher
import com.ammyt.kguedr.CONSTANT_APIKEY
import com.ammyt.kguedr.PREFERENCES_SHOW_CELSIUS
import com.ammyt.kguedr.R
import com.ammyt.kguedr.activity.DetailActivity
import com.ammyt.kguedr.activity.SettingsActivity
import com.ammyt.kguedr.adapter.ForecastRecyclerViewAdapter
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
    lateinit var viewSwitcher: ViewSwitcher
    lateinit var forecastList: RecyclerView

    var city: City? = null
        set(value) {
            field = value
            value?.let {
                forecast = value.forecast
            }
        }

    var forecast: List<Forecast>? = null
        set(value) {
            // "field" es el lugar donde realmente se guarda el value. De esta forma le asignamos
            // el valor "value" a "forecast" ANTES de hacer el resto
            field = value

            // Model -> View
            if (value != null) {
                // Ya tenemos datos. Asignamos el adapter (creado por nosotros) al RecyclerView
                val adapter = ForecastRecyclerViewAdapter(value, temperatureUnits())
                forecastList.adapter = adapter

                // Le decimos al RecyclerView que nos informe cuando pulsen en una de sus vistas
                adapter.onClickListener = View.OnClickListener { v: View? ->
                    // Nos han pulsado en una de las vistas
                    val position = forecastList.getChildAdapterPosition(v)
                    val forecastToShow = value[position]
                    val day = v?.findViewById<TextView>(R.id.day)?.text.toString()

                    // Lanzamos la actividad detalle (no se debería lanzar desde el fragment)
                    startActivity(DetailActivity.intent(activity, city?.name, day, forecastToShow))
                }

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

            // RECYCLER VIEW ~ CollectionView de iOS
            // 1) Accedemos al RecyclerView
            forecastList = root.findViewById(R.id.forecast_list)

            // 2) Le indicamos cómo debe visualiazarse (LayoutManager)
            forecastList.layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.recycler_colums))
//            forecastList.layoutManager = LinearLayoutManager(activity)

            // 3) Le indicamos cómo debe animarse (ItemAnimator)
            forecastList.itemAnimator = DefaultItemAnimator()

            // 4) Le añadimos su adapter (pero solo cuando haya datos -> no lo hacemos aquí sino en el setter del forecast)


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
            val newForecast: Deferred<List<Forecast>?> = bg {
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

    private fun downloadForecast(city: City?): List<Forecast>? {
        try {
            // Para pruebas de simulación de retardo en descarga y testeo de problemas
            Thread.sleep(1000)

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
            val forecasts = mutableListOf<Forecast>()

            for (dayIndex in 0..list.length() - 1) {

                val today = list.getJSONObject(dayIndex)

                val jsonMaxTemp = today.getJSONObject("temp").getDouble("max").toFloat()
                val jsonMinTemp = today.getJSONObject("temp").getDouble("min").toFloat()
                val jsonHumidity = today.getDouble("humidity").toFloat()
                val jsonDescription = today.getJSONArray("weather").getJSONObject(0).getString("description")
                var jsonIconString = today.getJSONArray("weather").getJSONObject(0).getString("icon")

                // Convertimos el iconString a un Drawble
                jsonIconString = jsonIconString.substring(0, 2)
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

                // Una vez que tenemos todos los datos, creamos nuestro Forecast y lo añadimos a la lista
                forecasts.add(Forecast(
                        jsonMaxTemp,
                        jsonMinTemp,
                        jsonHumidity,
                        jsonDescription,
                        iconResource
                ))
            }

            // Después de recorrer toda la lista de forecast, devolvemos nestra lista de Forecast
            return forecasts

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        // En caso de error no devolvemos nada
        return null
    }

    private fun updateTemperature() {
        // Actualizamos la temperatura de nuestros ViewHolder/CardViews. Para ello se lo indicamos al RecyclerView
        forecastList.adapter = ForecastRecyclerViewAdapter(forecast, temperatureUnits())

//        forecastList.adapter.notifyDataSetChanged()
    }

    private fun temperatureUnits() =
            if (PreferenceManager.getDefaultSharedPreferences(activity)
                    .getBoolean(PREFERENCES_SHOW_CELSIUS, true)) {
                Forecast.TempUnit.CELSIUS
            }
            else {
                Forecast.TempUnit.FAHRENHEIT
            }
}