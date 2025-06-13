package com.example.helpmycar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.database.FirebaseDatabase
// import com.google.firebase.storage.FirebaseStorage

class MechanicSignupActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private val IMAGE_PICK_CODE = 1001
    private var selectedImageUri: Uri? = null

    // Firebase setup (commented for now)
    // private lateinit var auth: FirebaseAuth
    // private val database = FirebaseDatabase.getInstance().getReference("Mechanics")
    // private val storage = FirebaseStorage.getInstance().reference.child("mechanic_profiles")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_signup)

        // UI Component Bindings
        profileImage = findViewById(R.id.profileImage)
        val addImageIcon = findViewById<ImageView>(R.id.addImageIcon)
        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val cnicEditText = findViewById<EditText>(R.id.editTextCNIC)
        val workshopEditText = findViewById<EditText>(R.id.editTextWorkshop)
        val expertiseEditText = findViewById<EditText>(R.id.editTextExpertise)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        // Firebase init (commented for now)
        // auth = FirebaseAuth.getInstance()

        addImageIcon.setOnClickListener {
            pickImageFromGallery()
        }

        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        nextButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val cnic = cnicEditText.text.toString().trim()
            val workshop = workshopEditText.text.toString().trim()
            val expertise = expertiseEditText.text.toString().trim()

            if (name.isEmpty() || cnic.isEmpty() || workshop.isEmpty() || expertise.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Placeholder next step (will be replaced with Firebase logic later)
            Toast.makeText(this, "Mechanic info collected. Firebase integration pending.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MechanicSignupActivity::class.java))  // Make sure this activity exists

            /*
            val userId = auth.currentUser?.uid
            val mechanicData = mapOf(
                "name" to name,
                "cnic" to cnic,
                "workshop" to workshop,
                "expertise" to expertise
            )

            if (selectedImageUri != null) {
                val imageRef = storage.child("$userId.jpg")
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val mechanicWithImage = mechanicData + ("profileImageUrl" to uri.toString())
                            userId?.let {
                                database.child(it).setValue(mechanicWithImage)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Signup success", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, MechanicSignupStep2Activity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "DB save failed", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
            }
            */
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImage.setImageURI(selectedImageUri)
        }
    }
}
