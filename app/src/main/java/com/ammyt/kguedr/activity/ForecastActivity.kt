package com.ammyt.kguedr.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ammyt.kguedr.R
import com.ammyt.kguedr.fragment.CityListFragment
import com.ammyt.kguedr.model.Cities
import com.ammyt.kguedr.model.City

class ForecastActivity : AppCompatActivity(), CityListFragment.OnCitySelectedListener{
//    val TAG = ForecastActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        // SIEMPRE antes de crear un fragment, debemos comprobar si ya
        // hemos añadido éste a nuestra jerarquía
        if (fragmentManager.findFragmentById(R.id.city_list_fragment) == null) {
            val fragment = CityListFragment.newInstance()
            fragmentManager.beginTransaction()
                    .add(R.id.city_list_fragment, fragment)
                    .commit()
        }
    }

    override fun onCitySelected(city: City?, position: Int) {
        startActivity(CityPagerActivity.intent(this, position))
    }
}
