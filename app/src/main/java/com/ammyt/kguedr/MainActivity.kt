package com.ammyt.kguedr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.stone_button).setOnClickListener(this)
        findViewById<Button>(R.id.donkey_button).setOnClickListener(this)

        Log.v(TAG, "🔎 Testing onCreate.")

        if (savedInstanceState != null) {
            Log.v(TAG, "🆗 savedInstanceState NO es null y vale: ${savedInstanceState.getString("clave")}")
        }
        else {
            Log.v(TAG, "🔎 savedInstanceState ES null")
        }
    }

    override fun onClick(v: View?) {

        Log.v(TAG, when(v?.id) {
            R.id.stone_button   ->  "🖱 onClick STONE"
            R.id.donkey_button  ->  "🖱 onClick DONKEY"
            else                ->  "🖱 onClick desconocido"
        })

//        OPCIÓN - 3
//        when (v?.id) {
//            R.id.stone_button   ->  Log.v(TAG, "🖱 onClick STONE")
//            R.id.donkey_button  ->  Log.v(TAG, "🖱 onClick DONKEY")
//            else                ->  Log.v(TAG, "🖱 onClick desconocido")
//        }

//        OPCIÓN - 2
//        if (v != null) {
//            if (v.id == R.id.stone_button) {
//                Log.v(TAG, "🖱 onClick STONE")
//            } else {
//                Log.v(TAG, "🖱 onClick DONKEY")
//            }
//        }

//        OPCIÓN - 1
//        if (v == stoneButton) {
//            Log.v(TAG, "🖱 onClick STONE")
//        }
//        else if (v == donkeyButton) {
//            Log.v(TAG, "🖱 onClick DONKEY")
//        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        Log.v(TAG, "🔎 onSaveInstanceState.")

        outState?.putString("clave", "valor")
    }
}
