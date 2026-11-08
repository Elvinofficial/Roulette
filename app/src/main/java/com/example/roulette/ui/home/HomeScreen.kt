
package com.example.roulette.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roulette.ui.components.RouletteButton

private val Graphite = Color(0xFF242424)
private val SecondaryText = Color(0xFF666666)
private val LightSurface = Color(0xFFF5F5F5)

@Composable
fun HomeScreen(
    onMeasureClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
            .padding(20.dp)
    ) {
        // Название приложения
        Text(
            text = "Рулетка",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Graphite
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Инструкция
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Как пользоваться?",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Graphite
            )

            InstructionCard(
                number = 1,
                title = "Найдите поверхность",
                description = "Наведите камеру на хорошо освещённую " +
                        "поверхность. Жёлтое перекрестие означает поиск. " +
                        "Когда перекрестие станет зелёным, " +
                        "можно устанавливать точку."
            )

            InstructionCard(
                number = 2,
                title = "Установите точки",
                description = "Нажмите большую кнопку «+», чтобы поставить " +
                        "первую точку. Затем установите последующие точки. "
            )

            InstructionCard(
                number = 3,
                title = "Отмените действие",
                description = "Нажмите «Отменить», чтобы удалить " +
                        "последнюю установленную точку."
            )

            InstructionCard(
                number = 4,
                title = "Начните заново",
                description = "Нажмите «Очистить», чтобы удалить " +
                        "все точки текущего измерения."
            )

            Text(
                text = "Для первых измерений попробуйте " +
                        "расстояние около 1 метра. " +
                        "Двигайте смартфон плавно и избегайте " +
                        "плохого освещения.",
                fontSize = 14.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Основная кнопка
        RouletteButton(
            text = "Продолжить",
            onClick = onMeasureClick,
            primary = true,
            height = 60.dp,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InstructionCard(
    number: Int,
    title: String,
    description: String
) {
    Surface(
        color = LightSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$number. $title",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Graphite
            )

            Text(
                text = description,
                fontSize = 15.sp,
                color = SecondaryText,
                lineHeight = 22.sp
            )
        }
    }
}
