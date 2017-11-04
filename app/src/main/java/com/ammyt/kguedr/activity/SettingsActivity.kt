package com.ammyt.kguedr.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.RadioGroup
import com.ammyt.kguedr.R


class SettingsActivity : AppCompatActivity() {

    companion object {
        var EXTRA_UNITS = "units"

        fun intent(context: Context, units: Int): Intent {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(EXTRA_UNITS, units)

            return intent
        }

//        fun intent(context: Context): Intent {
//            return Intent(context, SettingsActivity::class.java)
//        }
    }

//    val radioGroup by lazy { findViewById<RadioGroup>(R.id.units_rg) }
    lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Le podemos añadir la arrowFunction 'v -> ' pero como no lo usamos, lo podemos dejar así
        findViewById<Button>(R.id.ok_btn).setOnClickListener { acceptSettings() }
        findViewById<Button>(R.id.cancel_btn).setOnClickListener { cancelSettings() }

        radioGroup = findViewById(R.id.units_rg)
        val rbSelected = intent.getIntExtra(EXTRA_UNITS, R.id.celsius_rb)
        radioGroup.check(rbSelected)


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
        returnIntent.putExtra(EXTRA_UNITS, radioGroup.checkedRadioButtonId)

        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}