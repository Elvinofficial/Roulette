package com.example.roulette.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roulette.ui.history.HistoryScreen
import com.example.roulette.ui.measurement.MeasurementScreen
import com.example.roulette.ui.measurement.MeasurementViewModel

private object Routes {
    const val MEASUREMENT = "measurement"
    const val HISTORY = "history"
}
 // управление навигацией
@Composable
fun RouletteNavigation(
    measurementViewModel: MeasurementViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MEASUREMENT // с какого экрана начинать
    ) {
        composable(Routes.MEASUREMENT) {
            MeasurementScreen(
                viewModel = measurementViewModel,
                onHistoryClick = {
                    navController.navigate(Routes.HISTORY)
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
