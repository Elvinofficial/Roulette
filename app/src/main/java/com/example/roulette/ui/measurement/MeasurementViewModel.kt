package com.example.roulette.ui.measurement

import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import com.example.roulette.domain.measurement.DistanceCalculator
import com.example.roulette.domain.model.MeasurePoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MeasurementViewModel : ViewModel() {

   // контейнер для хранения текущего состояния списка точек
    private val _points = MutableStateFlow<List<MeasurePoint>> ( emptyList()
    )

    val points: StateFlow<List<MeasurePoint>> = _points.asStateFlow()

    val distance: Float?
        get() {
            val currentPoints = _points.value

            if ( currentPoints.size != 2) { // пока простые 2 точки, дальше поменяется
                return null;
            }

            return DistanceCalculator.calculate(
                currentPoints[0],
                currentPoints[1]
            )
        }

    fun addPoint() {
        _points.update { currentPoints ->

            if (currentPoints.size >= 2) {
                return@update currentPoints
            }

            val demoPoints = listOf(
                MeasurePoint (0.20f, 0.5f), // сейчас захарткожены, потом уберется
                MeasurePoint(0.750f, 0.8f)
            )

            currentPoints + demoPoints [currentPoints.size]
        }
    }
    // убирает одну точку
    fun undoPoint() {
        _points.update { currentPoints -> currentPoints.dropLast(1) }
    }

    // очищает список точек
    fun clearPoints() {
        _points.value = emptyList()
    }
}