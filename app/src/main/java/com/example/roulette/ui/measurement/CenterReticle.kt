
package com.example.roulette.ui.measurement

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun CenterReticle(
    isReady: Boolean,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isReady) {
            Color(0xFF4CAF50) // Зелёный — можно ставить точку
        } else {
            Color(0xFFFFD54F) // Жёлтый — поиск поверхности
        },
        animationSpec = tween(durationMillis = 180),
        label = "ReticleColor"
    )

    Canvas(
        modifier = modifier.size(24.dp)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val inner = 4.dp.toPx()
        val outer = 10.dp.toPx()

        val segments = listOf(
            // Верх
            Offset(centerX, centerY - outer) to
                    Offset(centerX, centerY - inner),

            // Низ
            Offset(centerX, centerY + inner) to
                    Offset(centerX, centerY + outer),

            // Лево
            Offset(centerX - outer, centerY) to
                    Offset(centerX - inner, centerY),

            // Право
            Offset(centerX + inner, centerY) to
                    Offset(centerX + outer, centerY)
        )

        segments.forEach { (start, end) ->
            // Тёмная обводка для видимости на светлом фоне
            drawLine(
                color = Color.Black.copy(alpha = 0.8f),
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Основной цвет прицела
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
