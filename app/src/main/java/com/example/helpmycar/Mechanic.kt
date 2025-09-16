package com.example.helpmycar

data class Mechanic(
    val id: String = "",
    val name: String? = null,
    val level: String? = null,          // "Expert" | "Intermediate" | "Beginner"
    val expertise: String? = null,      // e.g., "AC"
    val ratingAvg: Double? = null,      // e.g., 4.6
    val ratingCount: Long? = null,      // e.g., 23
    val latitude: Double? = null,
    val longitude: Double? = null,
    val workshop: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val profileImageUrl: String? = null
)
