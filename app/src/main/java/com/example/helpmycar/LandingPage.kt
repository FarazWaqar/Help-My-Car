package com.example.helpmycar // Make sure this matches your package name

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landingscreen) // Make sure this matches your XML file name

        // Using findViewById
        val backArrowImageView: ImageView = findViewById(R.id.back_arrow)
        val loginButton: Button = findViewById(R.id.login_button_auth)
        val signupButton: Button = findViewById(R.id.signup_button_auth)

        // Handle back arrow click
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish() // Optional: remove this activity from the back stack
        }
/*
        // Handle Login button click
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // Replace with your LoginActivity
            startActivity(intent)
        }

        // Handle Sign Up button click
        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java) // Replace with your SignupActivity
            startActivity(intent)
        }
  */
    }
}