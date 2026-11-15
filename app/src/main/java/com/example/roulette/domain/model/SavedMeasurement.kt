package com.example.roulette.domain.model

data class SavedMeasurement(
    val id: Long = 0L,
    val name: String,
    val distanceMm: Int,
    val createdAtMillis: Long,
    val imageFileName: String
)