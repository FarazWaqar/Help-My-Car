package com.example.helpmycar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mechanic(
    val id: String = "",
    val name: String = "Mechanic",
    val expertise: String = "",
    val level: String = "Expert",      // "Expert" | "Intermediate" | "Beginner"
    val rating: Double = 4.5,
    val reviewsCount: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val profileImageUrl: String? = null,
    val workshop: String? = null,
    // computed at runtime
    var distanceMeters: Double = Double.MAX_VALUE
) : Parcelable
