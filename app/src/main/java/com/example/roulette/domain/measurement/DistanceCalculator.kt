package com.example.roulette.domain.measurement

import com.example.roulette.domain.model.MeasurePoint
import kotlin.math.sqrt

// Вычисление расстояния
object DistanceCalculator {

    fun calculate (
        first: MeasurePoint,
        second: MeasurePoint,
    ): Float {
        val dx = second.x - first.x
        val dy = second.y - first.y

        return sqrt(dx * dx + dy * dy) * 100f
    }
}