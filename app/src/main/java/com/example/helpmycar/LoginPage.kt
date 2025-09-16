package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
    private val TAG = "LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginscreen)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        forgetPasswordButton = findViewById(R.id.forgetPasswordButton)
        signupButton = findViewById(R.id.signupButton)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener { doLogin() }
        forgetPasswordButton.setOnClickListener { doResetPassword() }
        signupButton.setOnClickListener {
            // If you have a user-type selection screen, go there; else go to CustomerSignUpActivity
            startActivity(Intent(this, UserTypeSelectionActivity::class.java))
        }
    }

    private fun doLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Enter email and password!")
            return
        }

        Log.d(TAG, "Attempting login with $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    toast("Login failed: no UID")
                    return@addOnSuccessListener
                }
                fetchUserTypeAndRoute(uid)
            }
            .addOnFailureListener { e ->
                toast("Login failed: ${e.message}")
            }
    }

    private fun fetchUserTypeAndRoute(uid: String) {
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    toast("User profile not found!")
                    return@addOnSuccessListener
                }
                val userType = doc.getString("userType")
                Log.d("USER_TYPE", "User type: $userType")

                when (userType?.lowercase()) {
                    "customer" -> startActivity(Intent(this, CustomerHomeActivity::class.java))
                    "mechanic" -> startActivity(Intent(this, MechanicHomeActivity::class.java))
                    "parts supplier", "supplier" -> startActivity(Intent(this, CustomerHomeActivity::class.java))
                    "carwash", "car wash" -> startActivity(Intent(this, CustomerHomeActivity::class.java))
                    else -> {
                        toast("Unknown user type!")
                        return@addOnSuccessListener
                    }
                }
                finish()
            }
            .addOnFailureListener {
                toast("Failed to get user type!")
            }
    }

    private fun doResetPassword() {
        val email = emailEditText.text.toString().trim()
        if (email.isEmpty()) {
            toast("Enter your email for reset!")
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { toast("Password reset email sent!") }
            .addOnFailureListener { e -> toast("Error: ${e.message}") }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
