package com.example.helpmycar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.graphics.scale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getCurrentCustomerLocation { customerLatLng ->
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 13f))
            // Customer marker - red
            mMap.addMarker(
                MarkerOptions()
                    .position(customerLatLng)
                    .title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            showNearbyMechanics(customerLatLng)
        }
    }

    // Helper to get the current customer location from Firestore
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

    // Helper for resized marker icon
    private fun getResizedBitmapDescriptor(resId: Int, width: Int, height: Int): BitmapDescriptor {
        val bitmap = android.graphics.BitmapFactory.decodeResource(resources, resId)
        val smallBitmap = bitmap.scale(width, height, false)
        return BitmapDescriptorFactory.fromBitmap(smallBitmap)
    }

    // Show only mechanics (userType = mechanic) in 10km
    private fun showNearbyMechanics(customerLatLng: LatLng) {
        db.collection("users").get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val userType = doc.getString("userType") ?: continue
                    if (userType != "mechanic") continue  // Only mechanics
                    val lat = doc.getDouble("latitude") ?: continue
                    val lng = doc.getDouble("longitude") ?: continue
                    val name = doc.getString("name") ?: "Mechanic"
                    val expertise = doc.getString("expertise") ?: ""

                    Log.d("MapsActivity", "Mechanic: $name, Expertise: $expertise")

                    // Distance calculation
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        customerLatLng.latitude, customerLatLng.longitude,
                        lat, lng, results
                    )
                    if (results[0] <= 10000) { // within 10km
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(lat, lng))
                                .title(name)
                                .snippet(expertise)
                                .icon(getResizedBitmapDescriptor(R.drawable.wrench_marker, 64, 64))
                        )
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Could not load mechanics!", Toast.LENGTH_SHORT).show()
            }
    }
}
