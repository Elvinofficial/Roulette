
package com.example.roulette.di

import android.content.Context
import androidx.room.Room
import com.example.roulette.data.local.AppDatabase
import com.example.roulette.data.repository.MeasurementRepository
import com.example.roulette.data.storage.ImageStorage

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "roulette_database"
        ).build()
    }

    private val imageStorage: ImageStorage by lazy {
        ImageStorage(appContext)
    }

    val measurementRepository: MeasurementRepository by lazy {
        MeasurementRepository(
            dao = database.measurementDao(),
            imageStorage = imageStorage
        )
    }
}
