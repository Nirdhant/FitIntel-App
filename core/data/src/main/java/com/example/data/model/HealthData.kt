package com.example.data.model

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class HealthData(
    val age: Int?,
    val bloodGroup: String?,
    val bmi: Float?,
    val sugarLevel: Float?,
    val hemoglobinLevel: Float?,
    val cholesterolLevel: Float?,
    val heartRate: Int?,
    val caloriesBurned: Int?
): Parcelable