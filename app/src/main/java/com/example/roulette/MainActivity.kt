
package com.example.roulette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

private val White = Color(0xFFFFFFFF)
private val Graphite = Color(0xFF242424)
private val LightGray = Color(0xFFF5F5F5)
private val BorderGray = Color(0xFFE8E8E8)
private val SecondaryText = Color(0xFF858585)

data class MeasurePoint(
    val x: Float,
    val y: Float
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Graphite,
                    onPrimary = White,
                    background = White,
                    onBackground = Graphite,
                    surface = White,
                    onSurface = Graphite
                )
            ) {
                RouletteScreen()
            }
        }
    }
}

@Composable
fun RouletteScreen() {

    val points = remember {
        mutableStateListOf<MeasurePoint>()
    }

    val demoPoints = listOf(
        MeasurePoint(0.20f, 0.55f),
        MeasurePoint(0.75f, 0.55f)
    )

    val distance = if (points.size == 2) {
        val dx = points[1].x - points[0].x
        val dy = points[1].y - points[0].y

        sqrt(dx * dx + dy * dy) * 100
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .safeDrawingPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Рулетка",
                    color = Graphite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = LightGray,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "История",
                    color = Graphite,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // Область измерения
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    LightGray,
                    RoundedCornerShape(24.dp)
                )
                .border(
                    1.dp,
                    BorderGray,
                    RoundedCornerShape(24.dp)
                )
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val coordinates = points.map {
                    Offset(
                        x = it.x * size.width,
                        y = it.y * size.height
                    )
                }

                if (coordinates.size == 2) {
                    drawLine(
                        color = Graphite,
                        start = coordinates[0],
                        end = coordinates[1],
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                coordinates.forEach { point ->
                    drawCircle(
                        color = White,
                        radius = 10.dp.toPx(),
                        center = point
                    )

                    drawCircle(
                        color = Graphite,
                        radius = 7.dp.toPx(),
                        center = point
                    )
                }
            }

            if (distance != null) {
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    color = Graphite,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "%.1f мм.".format(distance),
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                    )
                }
            }

            if (points.isEmpty()) {
                Text(
                    text = "Добавьте первую точку",
                    color = SecondaryText,
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }


        }

        Text(
            text = "Точек: ${points.size} / 2",
            color = SecondaryText,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Кнопки управления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = {
                    if (points.isNotEmpty()) {
                        points.removeAt(points.lastIndex)
                    }
                },
                enabled = points.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGray,
                    contentColor = Graphite,
                    disabledContainerColor = LightGray,
                    disabledContentColor = SecondaryText
                )
            ) {
                Text("Отменить")
            }

            Button(
                onClick = {
                    if (points.size < 2) {
                        points.add(demoPoints[points.size])
                    }
                },
                enabled = points.size < 2,
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Graphite,
                    contentColor = White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp
                )
            }

            Button(
                onClick = { points.clear() },
                enabled = points.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGray,
                    contentColor = Graphite,
                    disabledContainerColor = LightGray,
                    disabledContentColor = SecondaryText
                )
            ) {
                Text("Очистить")
            }
        }

        Button(
            onClick = { },
            enabled = distance != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Graphite,
                contentColor = White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Сохранить измерение",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
