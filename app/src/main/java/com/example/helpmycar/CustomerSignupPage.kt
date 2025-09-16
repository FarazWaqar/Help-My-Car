package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CustomerSignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var carModelEditText: EditText
    private lateinit var mileageEditText: EditText
    private lateinit var lastMaintenanceEditText: EditText
    private lateinit var dailyDrivingEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nextButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customersignup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.editTextName)
        phoneEditText = findViewById(R.id.editTextPhone)
        carModelEditText = findViewById(R.id.editTextCarModel)
        mileageEditText = findViewById(R.id.editTextMileage)
        lastMaintenanceEditText = findViewById(R.id.editTextLastMaintenance)
        dailyDrivingEditText = findViewById(R.id.editTextDailyDriving)
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        nextButton = findViewById(R.id.nextButton)
        backArrow = findViewById(R.id.backArrow)
        progress = findViewById(R.id.progress)

        backArrow.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        nextButton.setOnClickListener { attemptSignup() }
    }

    private fun attemptSignup() {
        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val carModel = carModelEditText.text.toString().trim()
        val mileage = mileageEditText.text.toString().trim()
        val lastMaintenance = lastMaintenanceEditText.text.toString().trim()
        val dailyDriving = dailyDrivingEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString() // keep exact length

        if (name.isEmpty() || phone.isEmpty() || carModel.isEmpty() || mileage.isEmpty()
            || lastMaintenance.isEmpty() || dailyDriving.isEmpty()
            || email.isEmpty() || password.isEmpty()
        ) {
            toast("Please fill in all fields")
            return
        }
        if (password.length < 6) {
            toast("Password must be at least 6 characters")
            return
        }

        setLoading(true)

        // 1) Create account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    setLoading(false)
                    toast("Unexpected error: no UID")
                    return@addOnSuccessListener
                }
                // 2) Save profile
                saveProfile(uid, name, phone, carModel, mileage, lastMaintenance, dailyDriving, email, merge = false)
            }
            .addOnFailureListener { e ->
                // If email exists, sign in then save/merge profile
                if (e is FirebaseAuthUserCollisionException) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val uid = auth.currentUser?.uid
                            if (uid == null) {
                                setLoading(false)
                                toast("Unexpected error: no UID")
                                return@addOnSuccessListener
                            }
                            saveProfile(uid, name, phone, carModel, mileage, lastMaintenance, dailyDriving, email, merge = true)
                        }
                        .addOnFailureListener { signInErr ->
                            setLoading(false)
                            toast("Login failed: ${signInErr.message}")
                        }
                } else {
                    setLoading(false)
                    toast("Auth failed: ${e.message}")
                }
            }
    }

    private fun saveProfile(
        uid: String,
        name: String,
        phone: String,
        carModel: String,
        mileage: String,
        lastMaintenance: String,
        dailyDriving: String,
        email: String,
        merge: Boolean
    ) {
        val data = hashMapOf(
            "name" to name,
            "phone" to phone,
            "carModel" to carModel,
            "mileage" to mileage,
            "lastMaintenance" to lastMaintenance,
            "dailyDriving" to dailyDriving,
            "email" to email,
            "userType" to "customer",
            "updatedAt" to Timestamp.now()
        )

        val options = if (merge) SetOptions.merge() else SetOptions.merge() // safe default

        firestore.collection("users").document(uid)
            .set(data, options)
            .addOnSuccessListener {
                setLoading(false)
                toast("Signup successful!")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { err ->
                setLoading(false)
                // If you see PERMISSION_DENIED here -> publish Firestore rules & ensure project matches
                toast("Failed to save profile: ${err.message}")
            }
    }

    private fun setLoading(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        nextButton.isEnabled = !loading
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
