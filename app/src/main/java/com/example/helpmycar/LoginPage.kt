package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginscreen)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val forgetPasswordText: TextView = findViewById(R.id.forgetPasswordText)
        val signupText: TextView = findViewById(R.id.signupText)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Integrate Firebase Authentication here
                // FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                //     .addOnCompleteListener { ... }
                Toast.makeText(this, "Login functionality coming soon!", Toast.LENGTH_SHORT).show()
            }
        }
/*
        forgetPasswordText.setOnClickListener {
            // Navigate to ForgetPasswordActivity
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        signupText.setOnClickListener {
            // Navigate to SignupActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

 */
    }
}
