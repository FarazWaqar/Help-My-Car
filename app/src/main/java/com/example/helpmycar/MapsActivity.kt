package com.example.helpmycar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var openSingle = false
    private var selectedLatLng: LatLng? = null
    private var selectedName: String? = null
    private var selectedSub: String? = null
    private var selectedDistanceKm: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)

        // read extras for single-mechanic mode
        openSingle = intent.getBooleanExtra("OPEN_SINGLE", false)
        if (openSingle) {
            val lat = intent.getDoubleExtra("MECH_LAT", Double.NaN)
            val lng = intent.getDoubleExtra("MECH_LNG", Double.NaN)
            if (!lat.isNaN() && !lng.isNaN()) {
                selectedLatLng = LatLng(lat, lng)
            }
            selectedName = intent.getStringExtra("MECH_NAME")
            val level = intent.getStringExtra("MECH_LEVEL") ?: ""
            val exp = intent.getStringExtra("MECH_EXPERTISE") ?: ""
            selectedDistanceKm = intent.getDoubleExtra("MECH_DISTANCE_KM", Double.NaN)
            selectedSub = listOf(level, exp).filter { it.isNotBlank() }.joinToString(" • ")
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (openSingle && selectedLatLng != null) {
            showSingleMechanic()
        } else {
            // fallback: show you + (optionally) nearby markers
            getCurrentCustomerLocation { you ->
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(you, 13f))
                mMap.addMarker(MarkerOptions()
                    .position(you)
                    .title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                // You can also draw other mechanics here if you want.
            }
        }
    }

    private fun showSingleMechanic() {
        val pos = selectedLatLng!!
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
        mMap.addMarker(
            MarkerOptions()
                .position(pos)
                .title(selectedName ?: "Mechanic")
        )

        // show bottom sheet
        findViewById<TextView>(R.id.sheetName).text = selectedName ?: "Mechanic"
        findViewById<TextView>(R.id.sheetSub).text = selectedSub ?: ""
        findViewById<TextView>(R.id.sheetDistance).text =
            selectedDistanceKm?.let { String.format("%.1f km", it) } ?: ""
        findViewById<ImageView>(R.id.sheetAvatar) // (hook Glide here if you want)
        findViewById<android.view.View>(R.id.mechanicSheet).visibility = android.view.View.VISIBLE

        findViewById<Button>(R.id.btnNavigate).setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${pos.latitude},${pos.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    private fun getCurrentCustomerLocation(callback: (LatLng) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val lat = doc.getDouble("latitude") ?: 0.0
                val lng = doc.getDouble("longitude") ?: 0.0
                callback(LatLng(lat, lng))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Your location not found!", Toast.LENGTH_SHORT).show()
            }
    }
}
