package com.example.helpmycar
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class CustomerSignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customersignup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val carModelEditText = findViewById<EditText>(R.id.editTextCarModel)
        val mileageEditText = findViewById<EditText>(R.id.editTextMileage)
        val lastMaintenanceEditText = findViewById<EditText>(R.id.editTextLastMaintenance)
        val dailyDrivingEditText = findViewById<EditText>(R.id.editTextDailyDriving)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        nextButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val carModel = carModelEditText.text.toString().trim()
            val mileage = mileageEditText.text.toString().trim()
            val lastMaintenance = lastMaintenanceEditText.text.toString().trim()
            val dailyDriving = dailyDrivingEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || carModel.isEmpty() || mileage.isEmpty()
                || lastMaintenance.isEmpty() || dailyDriving.isEmpty()
                || email.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔐 Step 1: Create Firebase Auth user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                    // 🔄 Step 2: Save user data to Firestore
                    val customerData = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "carModel" to carModel,
                        "mileage" to mileage,
                        "lastMaintenance" to lastMaintenance,
                        "dailyDriving" to dailyDriving,
                        "email" to email,
                        "userType" to "customer"
                    )

                    firestore.collection("users").document(uid)
                        .set(customerData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                            // ✅ Go to Home or Dashboard
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save profile: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Auth failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
