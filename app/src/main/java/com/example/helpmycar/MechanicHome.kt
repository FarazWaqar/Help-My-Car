package com.example.helpmycar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MechanicHomeActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var buttonChatbot: Button
    private lateinit var buttonRequestService: Button
    private lateinit var buttonMaintenanceReminder: Button
    private lateinit var searchEditText: EditText

    // Nav buttons
    private lateinit var navHome: Button
    private lateinit var navLogs: Button
    private lateinit var navOffers: Button
    private lateinit var navProfile: Button

    private val LOCATION_PERMISSION_REQUEST = 222

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var isUpdatingLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mechanic_home)

        backArrow = findViewById(R.id.backArrow)
        buttonChatbot = findViewById(R.id.buttonChatbot)
        buttonRequestService = findViewById(R.id.buttonRequestService)
        buttonMaintenanceReminder = findViewById(R.id.buttonMaintenanceReminder)
        searchEditText = findViewById(R.id.searchEditText)

        navHome = findViewById(R.id.navHome)
        navLogs = findViewById(R.id.navLogs)
        navOffers = findViewById(R.id.navOffers)
        navProfile = findViewById(R.id.navProfile)

        backArrow.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        buttonChatbot.setOnClickListener { startActivity(Intent(this, ChatbotActivity::class.java)) }
        buttonRequestService.setOnClickListener { startActivity(Intent(this, RequestServiceActivity::class.java)) }
        buttonMaintenanceReminder.setOnClickListener { startActivity(Intent(this, MaintenanceReminderActivity::class.java)) }

        navHome.setOnClickListener { /* Already on Home */ }
        navLogs.setOnClickListener { startActivity(Intent(this, MechanicProfileActivity::class.java)); finish() }
        navOffers.setOnClickListener { startActivity(Intent(this, MechanicProfileActivity::class.java)); finish() }
        navProfile.setOnClickListener { startActivity(Intent(this, MechanicProfileActivity::class.java)); finish() }

        // Location setup
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        if (isUpdatingLocation) return
        isUpdatingLocation = true

        val locationRequest = LocationRequest.create().apply {
            interval = 10000L // 10 seconds
            fastestInterval = 5000L // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val updates = mapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude
                    )
                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .update(updates)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            mainLooper
        )
    }

    private fun stopLocationUpdates() {
        if (::fusedLocationClient.isInitialized && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
            isUpdatingLocation = false
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission required to update mechanic location.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
