package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UserTypeSelectionActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var listView: ListView
    private lateinit var nextButton: Button

    private var selectedUserType: String? = null
    private val userTypes = arrayOf("Customer", "Mechanic", "Parts Supplier", "Car Wash Provider")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usertype)

        backArrow = findViewById(R.id.back_arrow)
        listView = findViewById(R.id.user_type_list)
        nextButton = findViewById(R.id.next_button_user_type)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, userTypes)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedUserType = userTypes[position]
        }

        backArrow.setOnClickListener {
            finish() // or navigate to LandingActivity if needed
        }

        nextButton.setOnClickListener {
            if (selectedUserType != null) {
                val intent = when (selectedUserType) {
                    "Customer" -> Intent(this, CustomerSignUpActivity::class.java)
                    /*
                    "Mechanic" -> Intent(this, MechanicSignupActivity::class.java)
                    "Parts Supplier" -> Intent(this, SupplierSignupActivity::class.java)
                    "Car Wash Provider" -> Intent(this, CarWashSignupActivity::class.java)

                     */
                    else -> null
                }

                intent?.putExtra("userType", selectedUserType)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
