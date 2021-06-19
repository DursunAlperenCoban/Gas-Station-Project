package com.example.petrogps2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        my_return_to_main_button.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MapsActivity::class.java)
            intent.putExtra("SearchArea",my_slider.value.toString())
            startActivity(intent)
            //finish()
        }

        button123.setOnClickListener {
        textView123.text = my_slider.value.toString()
        }
    }

}