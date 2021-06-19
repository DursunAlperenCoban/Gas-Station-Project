package com.example.petrogps2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashScreenTimeOut = 3000
        val homeIntent = Intent(this@SplashScreenActivity,MapsActivity::class.java)

        Handler().postDelayed({
            startActivity(homeIntent)
            finish()
        },splashScreenTimeOut.toLong())

    }
}