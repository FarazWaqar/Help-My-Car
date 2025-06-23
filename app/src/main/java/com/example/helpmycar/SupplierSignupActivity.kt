package com.example.helpmycar

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
// Firebase imports (commented)
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.firestore.FirebaseFirestore

class SupplierSignupActivity : AppCompatActivity() {

    private lateinit var shopNameEditText: EditText
    private lateinit var ownerNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var cnicEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var backArrow: ImageView

    // Firebase instances (commented)
    // private lateinit var auth: FirebaseAuth
    // private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_signup)

        // Initialize views
        shopNameEditText = findViewById(R.id.editTextShopName)
        ownerNameEditText = findViewById(R.id.editTextOwnerName)
        phoneEditText = findViewById(R.id.editTextPhone)
        addressEditText = findViewById(R.id.editTextAddress)
        cnicEditText = findViewById(R.id.editTextCNIC)
        signupButton = findViewById(R.id.signupButton)
        backArrow = findViewById(R.id.backArrow)

        // Firebase initialization (commented)
        // auth = FirebaseAuth.getInstance()
        // db = FirebaseFirestore.getInstance()

        signupButton.setOnClickListener {
            val shopName = shopNameEditText.text.toString().trim()
            val ownerName = ownerNameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val cnic = cnicEditText.text.toString().trim()

            if (shopName.isEmpty() || ownerName.isEmpty() || phone.isEmpty()
                || address.isEmpty() || cnic.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()

                // Firebase Firestore registration logic (commented)
                /*
                val supplierData = hashMapOf(
                    "shopName" to shopName,
                    "ownerName" to ownerName,
                    "phone" to phone,
                    "address" to address,
                    "cnic" to cnic
                )

                db.collection("partSuppliers")
                    .add(supplierData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registered to Firebase!", Toast.LENGTH_SHORT).show()
                        // You can navigate to another screen here
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                */
            }
        }

        backArrow.setOnClickListener {
            finish() // close this activity
        }
    }
}
