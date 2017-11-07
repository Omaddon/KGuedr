package com.ammyt.kguedr.activity

import android.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v13.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.ammyt.kguedr.R
import com.ammyt.kguedr.fragment.ForecastFragment
import com.ammyt.kguedr.model.Cities

class CityPagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_pager)

        val pager = findViewById<ViewPager>(R.id.view_pager)
        val cities = Cities()

        // Necesitamos un "delegado"/adapter que vaya creando cada página
        // OJO, tenemos que añadir un librería (support-v13) para usar este nuevo Pager!!
        // De esta forma creamos una clase anónima
        val adapter = object : FragmentPagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                return ForecastFragment.newInstance(cities[position])
            }

            override fun getCount(): Int {
                return cities.count
            }

            override fun getPageTitle(position: Int): CharSequence = cities[position].name
        }

        pager.adapter = adapter
    }
}