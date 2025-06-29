package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MechanicProfileActivity : AppCompatActivity() {

    private lateinit var nameText: TextView
    private lateinit var cnicText: TextView
    private lateinit var workshopInfoText: TextView
    private lateinit var expertiseText: TextView
    private lateinit var emailText: TextView
    private lateinit var profilePic: ImageView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mechanic_profile)

        nameText = findViewById(R.id.nameText)
        cnicText = findViewById(R.id.cnicText)
        workshopInfoText = findViewById(R.id.workshopInfoText)
        expertiseText = findViewById(R.id.expertiseText)
        emailText = findViewById(R.id.emailText)
        profilePic = findViewById(R.id.profilePic)

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<Button>(R.id.editBtn).setOnClickListener { showEditDialog() }
        findViewById<ImageView>(R.id.addPicBtn).setOnClickListener { /* upload/change pic logic */ }

        // Bottom nav click listeners
        findViewById<Button>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, MechanicHomeActivity::class.java))
        }
        findViewById<Button>(R.id.navLogs).setOnClickListener {
            startActivity(Intent(this, MechanicHomeActivity::class.java))
        }
        findViewById<Button>(R.id.navProfile).setOnClickListener { }
        findViewById<Button>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, MechanicHomeActivity::class.java))
        }

        fetchProfile()
    }

    private fun fetchProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    nameText.text = doc.getString("name") ?: ""
                    cnicText.text = doc.getString("cnic") ?: ""
                    workshopInfoText.text = doc.getString("workshop") ?: ""
                    expertiseText.text = doc.getString("expertise") ?: ""
                    emailText.text = doc.getString("email") ?: ""
                    // Profile pic fetch logic (optional):
                    // val picUrl = doc.getString("profilePicUrl")
                    // loadProfilePic(picUrl)
                } else {
                    Toast.makeText(this, "Profile not found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_mechanic_profile, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
        val cnicEdit = dialogView.findViewById<EditText>(R.id.editCnic)
        val workshopEdit = dialogView.findViewById<EditText>(R.id.editWorkshopInfo)
        val expertiseEdit = dialogView.findViewById<EditText>(R.id.editExpertise)
        val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)

        // Fill with current values
        nameEdit.setText(nameText.text)
        cnicEdit.setText(cnicText.text)
        workshopEdit.setText(workshopInfoText.text)
        expertiseEdit.setText(expertiseText.text)
        emailEdit.setText(emailText.text)

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val updates = mapOf(
                        "name" to nameEdit.text.toString(),
                        "cnic" to cnicEdit.text.toString(),
                        "workshop" to workshopEdit.text.toString(),
                        "expertise" to expertiseEdit.text.toString(),
                        "email" to emailEdit.text.toString()
                    )
                    db.collection("users").document(userId).update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                            fetchProfile()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
