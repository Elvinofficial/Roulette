package com.example.roulette.ui.measurement

import android.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
                color = Color (0xFF242424),
                start = positions[0],
                end = positions [1],
                strokeWidth = 5f
            )
        }
    positions.forEach { position ->
        drawCircle(
            color = Color.White,
            radius = 11f,
            center = position
        )

        drawCircle(
            color = Color(0xFF242424),
            radius = 11f,
            center = position,
            style = Stroke (width = 4f)
        )
    }
    }
}