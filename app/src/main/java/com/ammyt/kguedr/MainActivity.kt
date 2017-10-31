package com.ammyt.kguedr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v(TAG, "🔎 Testing onCreate.")

        if (savedInstanceState != null) {
            Log.v(TAG, "savedInstanceState NO es null y vale: ${savedInstanceState.getString("clave")}")
        }
        else {
            Log.v(TAG, "savedInstanceState ES null")
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        Log.v(TAG, "🔎 onSaveInstanceState.")

        outState?.putString("clave", "valor")
    }
}
