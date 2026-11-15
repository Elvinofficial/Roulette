
package com.example.roulette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import com.example.roulette.ui.theme.RouletteTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.roulette.ui.measurement.MeasurementViewModel
import com.example.roulette.ui.measurement.MeasurementViewModelFactory
import com.example.roulette.ui.navigation.RouletteNavigation

class MainActivity : ComponentActivity() {

    // Создаём ViewModel через фабрику,
    // передавая ей Repository из AppContainer
    private val measurementViewModel: MeasurementViewModel by viewModels {
        MeasurementViewModelFactory(
            (application as RouletteApplication)
                .container
                .measurementRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RouletteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    RouletteNavigation(
                        measurementViewModel = measurementViewModel
                    )
                }
            }
        }
    }
}
