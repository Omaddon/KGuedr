package com.ammyt.kguedr.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

import com.ammyt.kguedr.R
import com.ammyt.kguedr.model.Cities
import com.ammyt.kguedr.model.City


class CityListFragment : Fragment() {

    lateinit var root: View
    private var onCitySelectedListener: OnCitySelectedListener? = null

    companion object {
        fun newInstance(): CityListFragment {
            val fragment = CityListFragment()

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        inflater?.let {
            root = inflater.inflate(R.layout.fragment_city_list, container, false)
            val list = root.findViewById<ListView>(R.id.city_list)

            // Tenemos que crear un "delegado" para la lista
            // Usaremos uno por defecto ya creado
            val adapter = ArrayAdapter<City>(
                    activity,
                    android.R.layout.simple_list_item_1,
                    Cities.toArray())
            list.adapter = adapter

            // Detectamos la pulsación sobre una celda de la lista
            list.setOnItemClickListener { parent, view, position, id ->
                // Avisamos al listener. Nos comunicaremos con la actividad mediante protocolo/interfaz.
                onCitySelectedListener?.onCitySelected(Cities.get(position), position)
            }
        }

        return root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        commonAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        onCitySelectedListener = null
    }

    fun commonAttach(listener: Any?) {
        // Comprobamos que nuestra actividad está implementando nuestro protocolo (para comunicarnos)
        if (listener is OnCitySelectedListener) {
            // Añadimos como listener de los clicks de las celdas al listener que nos pasan (la actividad)
            onCitySelectedListener = listener
        }
    }

    interface OnCitySelectedListener {
        fun onCitySelected(city: City?, position: Int)
    }

}
