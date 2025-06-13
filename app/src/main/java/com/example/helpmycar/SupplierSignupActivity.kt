package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
// import com.google.firebase.database.FirebaseDatabase

class SupplierSignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_signup)

        val shopEditText = findViewById<EditText>(R.id.editTextShopDetails)
        val deliveryEditText = findViewById<EditText>(R.id.editTextDeliveryArea)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        nextButton.setOnClickListener {
            val shop = shopEditText.text.toString().trim()
            val delivery = deliveryEditText.text.toString().trim()

            if (shop.isEmpty() || delivery.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Data collected. Firebase integration pending.", Toast.LENGTH_SHORT).show()

            // startActivity(Intent(this, NextActivity::class.java))

            /*
            val database = FirebaseDatabase.getInstance().getReference("Suppliers")
            val supplierId = "generate_unique_id_or_use_auth_uid"
            val supplierData = mapOf(
                "shop" to shop,
                "deliveryArea" to delivery
            )

            database.child(supplierId).setValue(supplierData)
            */
        }
    }
}
