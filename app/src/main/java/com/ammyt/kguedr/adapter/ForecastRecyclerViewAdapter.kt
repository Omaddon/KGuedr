package com.ammyt.kguedr.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ammyt.kguedr.R
import com.ammyt.kguedr.model.Forecast


class ForecastRecyclerViewAdapter(
        val forecast: List<Forecast>,
        val tempUnit: Forecast.TempUnit) : RecyclerView.Adapter<ForecastRecyclerViewAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.content_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder?, position: Int) {
        holder?.bindForecast(forecast[position], tempUnit, position)
    }

    override fun getItemCount(): Int {
        return forecast.size
    }

    // Creamos una clase interna, la cuál será el "hueco" donde colocaremos nuestras celdas
    // ItemView será nuestra CardView que reciclaremos para mostrar los 7 días
    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val forecastImage = itemView.findViewById<ImageView>(R.id.forecast_image)
        val maxTemp = itemView.findViewById<TextView>(R.id.max_temp)
        val minTemp = itemView.findViewById<TextView>(R.id.min_temp)
        val humidity = itemView.findViewById<TextView>(R.id.humidity)
        val description = itemView.findViewById<TextView>(R.id.forecast_description)
        val day = itemView.findViewById<TextView>(R.id.day)

        fun bindForecast(forecast: Forecast, tempUnit: Forecast.TempUnit, position: Int) {
            // Accedemos al contexto de la vista para poder acceder a getString
            val context = itemView.context

            // Actualizamos la vista con el modelo
            forecastImage.setImageResource(forecast.icon)
            description.text = forecast.description
            updateTemperature(forecast, tempUnit)
            humidity.text = context.getString(R.string.humidity_format, forecast.humidity)
            day.text = generateDayText(position)
        }

        private fun generateDayText(position: Int): String {
            return when (position) {
                0 -> "Today"
                1 -> "Tomorrow"
                2 -> "3 days"
                3 -> "4 days"
                4 -> "5 days"
                5 -> "6 days"
                6 -> "7 days"
                else -> "Whatever"
            }
        }

        private fun updateTemperature(forecast: Forecast, units: Forecast.TempUnit) {
            val unitsString = temperatureUnitsString(units)

            maxTemp.text = itemView.context.getString(R.string.max_temp_format, forecast.getMaxTemp(units), unitsString)
            minTemp.text = itemView.context.getString(R.string.min_temp_format, forecast.getMinTemp(units), unitsString)
        }

        private fun temperatureUnitsString(units: Forecast.TempUnit) =
                if (units == Forecast.TempUnit.CELSIUS) "ºC" else "ºF"
    }
}