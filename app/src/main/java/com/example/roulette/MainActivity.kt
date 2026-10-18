
package com.example.roulette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.roulette.ui.measurement.MeasurementScreen
import com.example.roulette.ui.measurement.MeasurementViewModel
import com.example.roulette.ui.navigation.RouletteNavigation

class MainActivity : ComponentActivity() {

    private val measurementViewModel: MeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                )
                {
                    RouletteNavigation(
                         measurementViewModel = measurementViewModel
                    )
                }
            }
        }
    }
}
