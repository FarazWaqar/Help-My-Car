package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.database.FirebaseDatabase

class CustomerSignUpActivity : AppCompatActivity() {

    // private lateinit var auth: FirebaseAuth
    // private val database = FirebaseDatabase.getInstance().getReference("Customers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customersignup)

        // auth = FirebaseAuth.getInstance()

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val carModelEditText = findViewById<EditText>(R.id.editTextCarModel)
        val mileageEditText = findViewById<EditText>(R.id.editTextMileage)
        val lastMaintenanceEditText = findViewById<EditText>(R.id.editTextLastMaintenance)
        val dailyDrivingEditText = findViewById<EditText>(R.id.editTextDailyDriving)
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

            if (name.isEmpty() || phone.isEmpty() || carModel.isEmpty() ||
                mileage.isEmpty() || lastMaintenance.isEmpty() || dailyDriving.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase logic (commented)
            /*
            val userId = auth.currentUser?.uid
            val customerData = mapOf(
                "name" to name,
                "phone" to phone,
                "carModel" to carModel,
                "mileage" to mileage,
                "lastMaintenance" to lastMaintenance,
                "dailyDriving" to dailyDriving
            )

            userId?.let {
                database.child(it).setValue(customerData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, NextActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                    }
            }
            */

            // Placeholder action
            Toast.makeText(this, "Signup data collected (Firebase disabled)", Toast.LENGTH_SHORT).show()
            //startActivity(Intent(this, NextActivity::class.java)) // Replace with your actual destination
        }
    }
}
