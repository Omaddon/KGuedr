package com.ammyt.kguedr.activity

import android.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v13.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.ammyt.kguedr.R
import com.ammyt.kguedr.fragment.ForecastFragment
import com.ammyt.kguedr.model.Cities

class CityPagerActivity : AppCompatActivity() {

    val pager by lazy { findViewById<ViewPager>(R.id.view_pager) }
    val cities = Cities()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_pager)

        // Configuramos la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setLogo(R.mipmap.ic_launcher)
        setSupportActionBar(toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.pager, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.previous   -> {
                pager.currentItem--
                true
            }
            R.id.next       -> {
                pager.currentItem++
                true
            }
            else            -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        invalidateOptionsMenu()

        val menuPrev = menu?.findItem(R.id.previous)
        val menuNext = menu?.findItem(R.id.next)

        menuPrev?.setEnabled(pager.currentItem > 0)
        menuNext?.setEnabled(pager.currentItem < cities.count - 1)

        return true
    }
}