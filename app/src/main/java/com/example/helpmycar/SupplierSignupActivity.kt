package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SupplierSignupActivity : AppCompatActivity() {

    private lateinit var shopNameEditText: EditText
    private lateinit var ownerNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var cnicEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var backArrow: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_signup)

        shopNameEditText = findViewById(R.id.editTextShopName)
        ownerNameEditText = findViewById(R.id.editTextOwnerName)
        phoneEditText = findViewById(R.id.editTextPhone)
        addressEditText = findViewById(R.id.editTextAddress)
        cnicEditText = findViewById(R.id.editTextCNIC)
        signupButton = findViewById(R.id.signupButton)
        backArrow = findViewById(R.id.backArrow)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        signupButton.setOnClickListener {
            val shopName = shopNameEditText.text.toString().trim()
            val ownerName = ownerNameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val cnic = cnicEditText.text.toString().trim()

            if (shopName.isEmpty() || ownerName.isEmpty() || phone.isEmpty() || address.isEmpty() || cnic.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = phone + "@supplier.com"
            val password = phone + "123"

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                    val supplierData = hashMapOf(
                        "shopName" to shopName,
                        "ownerName" to ownerName,
                        "phone" to phone,
                        "address" to address,
                        "cnic" to cnic,
                        "email" to email
                    )

                    db.collection("partSuppliers")
                        .document(uid)
                        .set(supplierData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        backArrow.setOnClickListener {
            finish()
        }
    }
}
