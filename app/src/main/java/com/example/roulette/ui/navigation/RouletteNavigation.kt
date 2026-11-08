package com.example.roulette.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roulette.ui.history.HistoryScreen
import com.example.roulette.ui.measurement.MeasurementScreen
import com.example.roulette.ui.measurement.MeasurementViewModel
import com.example.roulette.config.AppConfig
import com.example.roulette.ui.home.HomeScreen
import com.example.roulette.ui.guide.GuideScreen
private object Routes {
    const val MEASUREMENT = "measurement"
    const val HISTORY = "history"
    const val HOME = "home"
    const val GUIDE = "guide"
}
// управление навигацией
@Composable
fun RouletteNavigation(
    measurementViewModel: MeasurementViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME// с какого экрана начинать
    ) {

        // Приветственное меню
        composable(Routes.HOME) {
            HomeScreen(
                onMeasureClick = {
                    navController.navigate(Routes.MEASUREMENT)
                }
            )
        }

        // Экран измерений
        composable(Routes.MEASUREMENT) {
            MeasurementScreen(
                viewModel = measurementViewModel,
                onHistoryClick = {
                    navController.navigate(Routes.HISTORY)
                },
                onGuideClick = {
                    navController.navigate(Routes.GUIDE)
                }
            )
        }

        // Экран истории
        composable(Routes.HISTORY) {
            HistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Руководство
        composable(Routes.GUIDE) {
            GuideScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}