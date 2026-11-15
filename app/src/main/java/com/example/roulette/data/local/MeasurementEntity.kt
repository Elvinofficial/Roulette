
package com.example.roulette.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class MeasurementEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,

    val distanceMm: Int,

    val createdAtMillis: Long,

    val imageFileName: String
)
