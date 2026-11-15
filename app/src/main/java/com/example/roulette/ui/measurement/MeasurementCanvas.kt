package com.example.roulette.ui.measurement

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.roulette.domain.model.MeasurePoint


@Composable
fun MeasurementCanvas(
    points: List<MeasurePoint>,
    modifier: Modifier = Modifier
)
{
    Canvas(modifier = modifier.fillMaxSize()) {

        val positions = points.map { point -> // умножение для того чтобы в пикселях отобразить правильно
            Offset(
                x = point.x * size.width,
                y = point.y * size.width
            )
        }

        if (positions.size == 2) {
            drawLine(
                color = Color.Yellow,
                start = positions[0],
                end = positions [1],
                strokeWidth = 5f
            )
        }
        positions.forEach { position ->
            drawCircle(
                color = Color.Yellow,
                radius = 11f,
                center = position
            )
        }
    }
}
