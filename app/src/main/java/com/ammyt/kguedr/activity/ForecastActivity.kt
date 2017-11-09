package com.ammyt.kguedr.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ammyt.kguedr.R
import com.ammyt.kguedr.fragment.CityListFragment
import com.ammyt.kguedr.fragment.CityPagerFragment
import com.ammyt.kguedr.model.City

class ForecastActivity : AppCompatActivity(), CityListFragment.OnCitySelectedListener{
//    val TAG = ForecastActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        // Con esto podemos conocer los detalles físicos del dispositivo que se está ejecutando
        // Se mostrará aquí mismo en modo depuración los valores
        // ------------------------------------------------------------
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val dpWidth = (width / metrics.density).toInt()
        val dpHeight = (height / metrics.density).toInt()
        val model = Build.MODEL
        val androidVersion = Build.VERSION.SDK_INT
        val dpi = metrics.densityDpi
        // ------------------------------------------------------------

        // Según la orientación y tamaño de nuestro dispositivo, deberemos mostrar unos fragments u otros
        // Comprobamos qué FrameLayouts (los placeHolder) tenemos
        if (findViewById<View>(R.id.city_list_fragment) != null) {
            // SIEMPRE antes de crear un fragment, debemos comprobar si ya
            // hemos añadido éste a nuestra jerarquía
            if (fragmentManager.findFragmentById(R.id.city_list_fragment) == null) {
                val fragment = CityListFragment.newInstance()
                fragmentManager.beginTransaction()
                        .add(R.id.city_list_fragment, fragment)
                        .commit()
            }
        }

        if (findViewById<View>(R.id.fragment_city_pager) != null) {
            // SIEMPRE antes de crear un fragment, debemos comprobar si ya
            // hemos añadido éste a nuestra jerarquía
            if (fragmentManager.findFragmentById(R.id.fragment_city_pager) == null) {
                val fragment = CityPagerFragment.newInstance(0)
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_city_pager, fragment)
                        .commit()
            }
        }
    }

    override fun onCitySelected(city: City?, position: Int) {
        val cityPagerFragment = fragmentManager.findFragmentById(R.id.fragment_city_pager) as? CityPagerFragment

        if (cityPagerFragment == null) {
            // No tenemos un viewPager
            startActivity(CityPagerActivity.intent(this, position))
        }
        else {
            // Tenemos un viewPager, así que movemos el pager
            // Comunicación Actividad -> Fragment
            cityPagerFragment.moveToCity(position)

        }
    }
}
