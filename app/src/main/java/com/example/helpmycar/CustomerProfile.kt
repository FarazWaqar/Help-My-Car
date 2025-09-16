package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerProfileActivity : AppCompatActivity() {

    private lateinit var nameText: TextView
    private lateinit var phoneText: TextView
    private lateinit var carModelText: TextView
    private lateinit var mileageText: TextView
    private lateinit var dailyDrivingText: TextView
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customer_profile)

        nameText = findViewById(R.id.nameText)
        phoneText = findViewById(R.id.phoneText)
        carModelText = findViewById(R.id.carModelText)
        mileageText = findViewById(R.id.mileageText)
        dailyDrivingText = findViewById(R.id.dailyDrivingText)

        // Bottom nav click listeners
        findViewById<Button>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, CustomerHomeActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.navLogs).setOnClickListener {
            startActivity(Intent(this, CustomerHomeActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.navOffers).setOnClickListener {
            startActivity(Intent(this, CustomerHomeActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.navProfile).setOnClickListener {} // already here

        fetchProfile()
    }

    private fun fetchProfile() {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("PROFILE_FETCH", "Fetching profile for user: $userId")
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                Log.d("PROFILE_FETCH", "DocumentSnapshot data: ${doc.data}")
                if (doc != null && doc.exists()) {
                    val name = doc.getString("name")
                    val phone = doc.getString("phone")
                    val carModel = doc.getString("carModel")
                    val mileage = doc.getString("mileage")
                    val dailyDriving = doc.getString("dailyDriving")
                    // Log the result
                    android.util.Log.d("PROFILE_FETCH", "name=$name, phone=$phone, carModel=$carModel, mileage=$mileage, dailyDriving=$dailyDriving")
                    // Set on UI
                    nameText.text = name ?: ""
                    phoneText.text = phone ?: ""
                    carModelText.text = carModel ?: ""
                    mileageText.text = mileage ?: ""
                    dailyDrivingText.text = dailyDriving ?: ""
                } else {
                    Toast.makeText(this, "Profile not found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_customer_profile, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
        val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)
        val carModelEdit = dialogView.findViewById<EditText>(R.id.editCarModel)
        val mileageEdit = dialogView.findViewById<EditText>(R.id.editMileage)
        val dailyEdit = dialogView.findViewById<EditText>(R.id.editDailyDriving)

        // Fill with current values
        nameEdit.setText(nameText.text)
        phoneEdit.setText(phoneText.text)
        carModelEdit.setText(carModelText.text)
        mileageEdit.setText(mileageText.text)
        dailyEdit.setText(dailyDrivingText.text)

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updates = mapOf(
                    "name" to nameEdit.text.toString(),
                    "phone" to phoneEdit.text.toString(),
                    "carModel" to carModelEdit.text.toString(),
                    "mileage" to mileageEdit.text.toString(),
                    "dailyDriving" to dailyEdit.text.toString()
                )
                if (userId != null) {
                    db.collection("users").document(userId).update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                            fetchProfile()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
