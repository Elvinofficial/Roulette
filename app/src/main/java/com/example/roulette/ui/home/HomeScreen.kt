
package com.example.roulette.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.roulette.ui.components.RouletteButton

private val Graphite = Color(0xFF242424)
private val SecondaryText = Color(0xFF666666)
private val LightSurface = Color(0xFFF5F5F5)
private val IconSurface = Color(0xFFE8E8E8)

@Composable
fun HomeScreen(
    onMeasureClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        // Название приложения
        Text(
            text = "Мерило",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Graphite
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Краткое описание
        Text(
            text = "Измеряйте расстояния с помощью камеры смартфона",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Прокручиваемая область
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Карточка быстрого старта
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LightSurface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {


                    Text(
                        text = "Как сделать измерение?",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Graphite
                    )

                    QuickStep(
                        title = "Найдите поверхность",
                        description = "Наведите камеру на поверхность и дождитесь, пока перекрестие станет зелёным.",
                        isCrosshair = true
                    )

                    QuickStep(
                        icon = Icons.Default.Add,
                        title = "Поставьте две точки",
                        description = "Наведите перекрестие на начало отрезка и нажмите «+». Повторите действие для второй точки."
                    )

                    QuickStep(
                        icon = Icons.Default.CheckCircle,
                        title = "Получите расстояние",
                        description = "После установки второй точки приложение автоматически покажет расстояние между ними."
                    )

                    QuickStep(
                        icon = Icons.Default.Save,
                        title = "Сохраните результат",
                        description = "Нажмите «Сохранить измерение», чтобы сохранить результат и фотографию в историю."
                    )

                }
            }

            // Совет и информация о руководстве
            Text(
                text = "Совет: для первых измерений попробуйте " +
                        "расстояние около 1 метра. " +
                        "Перемещайте смартфон плавно.",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Основная кнопка
        RouletteButton(
            text = "Начать измерение",
            onClick = onMeasureClick,
            primary = true,
            height = 60.dp,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Один шаг краткой инструкции.
 */
@Composable
private fun QuickStep(
    title: String,
    description: String,
    icon: ImageVector? = null,
    isCrosshair: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Surface(
            color = IconSurface,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isCrosshair) {
                    CrosshairIcon()
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Graphite,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Graphite
            )

            Text(
                text = description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SecondaryText
            )
        }
    }
}

/**
 * Перекрестие, повторяющее форму прицела AR-рулетки.
 */
@Composable
private fun CrosshairIcon() {
    Canvas(
        modifier = Modifier.size(26.dp)
    ) {
        val center = Offset(
            size.width / 2f,
            size.height / 2f
        )

        val gap = 4.dp.toPx()
        val radius = 11.dp.toPx()
        val stroke = 2.dp.toPx()

        // Левая часть
        drawLine(
            color = Graphite,
            start = Offset(center.x - radius, center.y),
            end = Offset(center.x - gap, center.y),
            strokeWidth = stroke
        )

        // Правая часть
        drawLine(
            color = Graphite,
            start = Offset(center.x + gap, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = stroke
        )

        // Верхняя часть
        drawLine(
            color = Graphite,
            start = Offset(center.x, center.y - radius),
            end = Offset(center.x, center.y - gap),
            strokeWidth = stroke
        )

        // Нижняя часть
        drawLine(
            color = Graphite,
            start = Offset(center.x, center.y + gap),
            end = Offset(center.x, center.y + radius),
            strokeWidth = stroke
        )
    }
}
