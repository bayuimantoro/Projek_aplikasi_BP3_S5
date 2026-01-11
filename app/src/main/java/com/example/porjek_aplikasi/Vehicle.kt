package com.example.porjek_aplikasi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class VehicleType {
    MOBIL, MOTOR
}

@Parcelize
data class Vehicle(
    val name: String,
    val description: String,
    val image: Int,
    val specs: Map<String, String>,
    val type: VehicleType
) : Parcelable