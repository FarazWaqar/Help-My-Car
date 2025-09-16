package com.example.helpmycar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NearbyMechanicsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MechanicsAdapter
    private lateinit var progress: ProgressBar
    private lateinit var emptyText: TextView

    private val askPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            granted[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            load()
        } else {
            Toast.makeText(this, "Location permission denied. Using saved location.", Toast.LENGTH_LONG).show()
            load(useDeviceLocation = false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_mechanics)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        recycler = findViewById(R.id.recycler)
        progress = findViewById(R.id.progress)
        emptyText = findViewById(R.id.emptyText)

        adapter = MechanicsAdapter(emptyList()) { item ->
            val m = item.mechanic
            val i = Intent(this, MapsActivity::class.java).apply {
                putExtra("OPEN_SINGLE", true)
                putExtra("MECH_ID", m.id)
                putExtra("MECH_NAME", m.name)
                putExtra("MECH_LAT", m.latitude)
                putExtra("MECH_LNG", m.longitude)
                putExtra("MECH_LEVEL", m.level)
                putExtra("MECH_EXPERTISE", m.expertise)
                putExtra("MECH_RATING", m.ratingAvg ?: 0.0)
                putExtra("MECH_WORKSHOP", m.workshop)
                putExtra("MECH_DISTANCE_KM", item.distanceKm)
                putExtra("MECH_PROFILE_URL", m.profileImageUrl)
            }
            startActivity(i)
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Ask for location if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            load()
        } else {
            askPermission.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun load(useDeviceLocation: Boolean = true) {
        progress.visibility = View.VISIBLE
        emptyText.visibility = View.GONE

        fun withCustomerLocation(onLoc: (Location) -> Unit) {
            if (useDeviceLocation &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)) {

                val fused = LocationServices.getFusedLocationProviderClient(this)
                fused.lastLocation
                    .addOnSuccessListener { loc -> if (loc != null) onLoc(loc) else loadCustomerLocFromFirestore(onLoc) }
                    .addOnFailureListener { loadCustomerLocFromFirestore(onLoc) }
            } else {
                loadCustomerLocFromFirestore(onLoc)
            }
        }

        withCustomerLocation { customerLoc ->
            db.collection("users")
                .whereEqualTo("userType", "mechanic")
                .get()
                .addOnSuccessListener { snap ->
                    val items = snap.documents.mapNotNull { d ->
                        val m = d.toObject(Mechanic::class.java)?.copy(id = d.id) ?: return@mapNotNull null
                        val lat = m.latitude ?: return@mapNotNull null
                        val lng = m.longitude ?: return@mapNotNull null
                        val distKm = distanceKm(customerLoc.latitude, customerLoc.longitude, lat, lng)
                        MechanicItem(m, distKm)
                    }.sortedBy { it.distanceKm }

                    progress.visibility = View.GONE
                    if (items.isEmpty()) {
                        emptyText.visibility = View.VISIBLE
                        adapter.submit(emptyList())
                    } else {
                        adapter.submit(items)
                    }
                }
                .addOnFailureListener { e ->
                    progress.visibility = View.GONE
                    Toast.makeText(this, "Could not load mechanics: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("NearbyMechanics", "Error", e)
                }
        }
    }

    private fun loadCustomerLocFromFirestore(onLoc: (Location) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { d ->
                val lat = d.getDouble("latitude") ?: 0.0
                val lng = d.getDouble("longitude") ?: 0.0
                val loc = Location("firestore").apply { latitude = lat; longitude = lng }
                onLoc(loc)
            }
            .addOnFailureListener {
                progress.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
                Toast.makeText(this, "Your location not found.", Toast.LENGTH_LONG).show()
            }
    }

    private fun distanceKm(aLat: Double, aLng: Double, bLat: Double, bLng: Double): Double {
        val res = FloatArray(1)
        Location.distanceBetween(aLat, aLng, bLat, bLng, res)
        return res[0] / 1000.0
    }
}
