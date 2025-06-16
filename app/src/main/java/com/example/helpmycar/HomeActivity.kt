package com.example.helpmycar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
// import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var buttonChatbot: Button
    private lateinit var buttonFindMechanics: Button
    private lateinit var buttonRequestService: Button
    private lateinit var buttonMaintenanceReminder: Button
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        backArrow = findViewById(R.id.backArrow)
        buttonChatbot = findViewById(R.id.buttonChatbot)
        buttonFindMechanics = findViewById(R.id.buttonFindMechanics)
        buttonRequestService = findViewById(R.id.buttonRequestService)
        buttonMaintenanceReminder = findViewById(R.id.buttonMaintenanceReminder)
        searchEditText = findViewById(R.id.searchEditText)

        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        buttonChatbot.setOnClickListener {
            // TODO: Uncomment Firebase logic if needed
            /*
            val dbRef = FirebaseDatabase.getInstance().reference
            */
            startActivity(Intent(this, ChatbotActivity::class.java))
        }

        buttonFindMechanics.setOnClickListener {
            startActivity(Intent(this, NearbyMechanicsActivity::class.java))
        }

        buttonRequestService.setOnClickListener {
            startActivity(Intent(this, RequestServiceActivity::class.java))
        }

        buttonMaintenanceReminder.setOnClickListener {
            startActivity(Intent(this, MaintenanceReminderActivity::class.java))
        }
    }
}
