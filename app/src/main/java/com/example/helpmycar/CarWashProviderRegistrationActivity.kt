package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.database.FirebaseDatabase

import com.example.helpmycar.R  // ✅ Recommended explicit import for R (optional, but clear)

class CarWashProviderRegistrationActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var nextButton: Button
    private lateinit var editTextServiceAreas: EditText
    private lateinit var editTextEquipmentDetails: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_wash_provider_registration)

        // Find Views
        backArrow = findViewById(R.id.backArrow)
        nextButton = findViewById(R.id.nextButton)
        editTextServiceAreas = findViewById(R.id.editTextServiceAreas)
        editTextEquipmentDetails = findViewById(R.id.editTextEquipmentDetails)

        // Back Arrow Click
        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Next Button Click
        nextButton.setOnClickListener {
            val serviceAreas = editTextServiceAreas.text.toString().trim()
            val equipmentDetails = editTextEquipmentDetails.text.toString().trim()

            if (serviceAreas.isEmpty() || equipmentDetails.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Data collected. Firebase integration pending.", Toast.LENGTH_SHORT).show()

            // TODO: Uncomment when Firebase backend is ready:
            /*
            val databaseRef = FirebaseDatabase.getInstance().reference
            val currentUser = FirebaseAuth.getInstance().currentUser

            currentUser?.let {
                val providerId = it.uid
                val providerData = HashMap<String, Any>()
                providerData["serviceAreas"] = serviceAreas
                providerData["equipmentDetails"] = equipmentDetails

                databaseRef.child("CarWashProviders").child(providerId)
                    .updateChildren(providerData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, NextActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            */

            // For now: Dummy Next
            val intent = Intent(this, CarWashProviderRegistrationActivity::class.java) // TODO: replace with actual next screen
            startActivity(intent)
        }
    }
}
