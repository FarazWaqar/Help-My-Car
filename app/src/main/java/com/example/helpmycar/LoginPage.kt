package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgetPasswordButton: Button
    private lateinit var signupButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginscreen)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        forgetPasswordButton = findViewById(R.id.forgetPasswordButton)
        signupButton = findViewById(R.id.signupButton)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("LOGIN", "Attempting to log in with email: $email")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // GET USER TYPE FROM FIRESTORE
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(uid).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val userType = document.getString("userType")
                                        Log.d("USER_TYPE", "User type: $userType")
                                        when (userType) {
                                            "customer" -> {
                                                startActivity(Intent(this, CustomerHomeActivity::class.java))
                                            }
                                            "mechanic" -> {
                                                startActivity(Intent(this, MechanicHomeActivity::class.java))
                                            }
                                            "Parts Supplier" -> {
                                                startActivity(Intent(this, CustomerHomeActivity::class.java))
                                            }
                                            "carwash" -> {
                                                startActivity(Intent(this, CustomerHomeActivity::class.java))
                                            }
                                            else -> {
                                                Toast.makeText(this, "Unknown user type!", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                        finish()
                                    } else {
                                        Toast.makeText(this, "User profile not found!", Toast.LENGTH_LONG).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to get user type!", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(this, "Login failed! UID not found", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        forgetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email for reset!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        signupButton.setOnClickListener {
            // Go to Sign Up Activity
            startActivity(Intent(this, UserTypeSelectionActivity::class.java))
        }
    }
}
