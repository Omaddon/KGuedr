package com.ammyt.kguedr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.RadioGroup


class SettingsActivity : AppCompatActivity() {

    companion object {
        var EXTRA_UNITS = "units"

        fun intent(context: Context) = Intent(context, SettingsActivity::class.java)

//        fun intent(context: Context): Intent {
//            return Intent(context, SettingsActivity::class.java)
//        }
    }

    var radioGroup: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Le podemos añadir la arrowFunction 'v -> ' pero como no lo usamos, lo podemos dejar así
        findViewById<Button>(R.id.ok_btn).setOnClickListener { acceptSettings() }
        findViewById<Button>(R.id.cancel_btn).setOnClickListener { cancelSettings() }

        radioGroup = findViewById(R.id.units_rg)

       // Equivalente a una clase anónima (closure) de Java en kotlin
//        findViewById<Button>(R.id.ok_btn).setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                acceptSettings()
//            }
//        })
    }

    private fun cancelSettings() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun acceptSettings() {
        val returnIntent = Intent()
        returnIntent.putExtra(EXTRA_UNITS, radioGroup?.checkedRadioButtonId)

        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}