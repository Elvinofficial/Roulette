
package com.example.roulette.ui.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roulette.data.repository.MeasurementRepository

class MeasurementViewModelFactory(
    private val repository: MeasurementRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                MeasurementViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return MeasurementViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
