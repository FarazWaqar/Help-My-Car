// ✅ Final Kotlin Code: CarWashProviderRegistrationActivity.kt
package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CarWashProviderRegistrationActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var nextButton: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextCNIC: EditText
    private lateinit var editTextServiceAreas: EditText
    private lateinit var editTextEquipmentDetails: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_wash_provider_registration)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize Views
        backArrow = findViewById(R.id.backArrow)
        nextButton = findViewById(R.id.nextButton)
        editTextName = findViewById(R.id.editTextName)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextCNIC = findViewById(R.id.editTextCNIC)
        editTextServiceAreas = findViewById(R.id.editTextServiceAreas)
        editTextEquipmentDetails = findViewById(R.id.editTextEquipmentDetails)

        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        nextButton.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val phone = editTextPhone.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val cnic = editTextCNIC.text.toString().trim()
            val serviceAreas = editTextServiceAreas.text.toString().trim()
            val equipmentDetails = editTextEquipmentDetails.text.toString().trim()
            val password = "${phone}123" // default password

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || cnic.isEmpty() ||
                serviceAreas.isEmpty() || equipmentDetails.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register with Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                    val carWashData = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "email" to email,
                        "cnic" to cnic,
                        "serviceAreas" to serviceAreas,
                        "equipmentDetails" to equipmentDetails,
                        "userType" to "carwash"
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(carWashData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Car Wash Provider Registered!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Auth Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
