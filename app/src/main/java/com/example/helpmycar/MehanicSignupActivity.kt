package com.example.helpmycar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MechanicSignupActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private val IMAGE_PICK_CODE = 1001
    private var selectedImageUri: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_signup)

        // UI Bindings
        profileImage = findViewById(R.id.profileImage)
        val addImageIcon = findViewById<ImageView>(R.id.addImageIcon)
        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val cnicEditText = findViewById<EditText>(R.id.editTextCNIC)
        val workshopEditText = findViewById<EditText>(R.id.editTextWorkshop)
        val expertiseEditText = findViewById<EditText>(R.id.editTextExpertise)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        // Firebase Init
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

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
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || cnic.isEmpty() || workshop.isEmpty() || expertise.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                    fun saveDataToFirestore(imageUrl: String?) {
                        val mechanicData = hashMapOf(
                            "name" to name,
                            "cnic" to cnic,
                            "workshop" to workshop,
                            "expertise" to expertise,
                            "email" to email,
                            "profileImageUrl" to imageUrl
                        )

                        firestore.collection("Mechanics").document(uid)
                            .set(mechanicData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                            }
                    }

                    if (selectedImageUri != null) {
                        val imageRef = storage.reference.child("mechanic_profiles/$uid.jpg")
                        imageRef.putFile(selectedImageUri!!)
                            .addOnSuccessListener {
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    saveDataToFirestore(uri.toString())
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                                saveDataToFirestore(null)
                            }
                    } else {
                        saveDataToFirestore(null)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
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
