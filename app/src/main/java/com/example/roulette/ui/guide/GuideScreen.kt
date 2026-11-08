
package com.example.roulette.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.roulette.ui.components.RouletteButton

// Цветовая палитра приложения
private val Graphite = Color(0xFF242424)
private val SecondaryText = Color(0xFF666666)
private val LightSurface = Color(0xFFF5F5F5)

@Composable
fun GuideScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        // ВЕРХНЯЯ ПАНЕЛЬ
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Кнопка возврата на предыдущий экран
            RouletteButton(
                text = "Назад",
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(100.dp),
                height = 44.dp,
                fontSize = 14.sp
            )

            // Название и версия приложения
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Рулетка",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Graphite
                )

                Text(
                    text = "Версия 0.1",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ПРОКРУЧИВАЕМОЕ СОДЕРЖИМОЕ
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "Руководство пользователя",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Graphite
            )

            Text(
                text = "В этом руководстве описаны порядок выполнения измерений, назначение " +
                        "кнопок управления и рекомендации по использованию приложения.",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(3.dp))

            SectionTitle("Порядок измерения")

            GuideCard(
                title = "1. Подготовка",
                description = "При первом запуске разрешите приложению доступ к камере."
            )

            GuideCard(
                title = "2. Поиск поверхности",
                description = "Наведите камеру на хорошо освещённую " +
                        "поверхность и плавно перемещайте смартфон. " +
                        "Жёлтое перекрестие означает поиск, " +
                        "когда оно станет зелёным, можно " +
                        "устанавливать точку."
            )

            GuideCard(
                title = "3. Установка точек",
                description = "Совместите центр перекрестия с началом измеряемой поверхности и нажмите «+». " +
                        "Затем наведите камеру на конечную позицию и повторно нажмите «+»."
            )

            GuideCard(
                title = "4. Получение результата",
                description = "После установки двух точек приложение соединит их линией " +
                        "и автоматически отобразит расстояние. "
            )

            Spacer(modifier = Modifier.height(3.dp))

            // ОПИСАНИЕ КНОПОК
            SectionTitle("Кнопки управления")

            GuideCard(
                title = "«+» — добавить точку",
                description = "Устанавливает новую точку в центре перекрестия,когда система " +
                        "определила подходящее положение. "
            )

            GuideCard(
                title = "«Отменить»",
                description = "Удаляет последнюю установленную точку. Позволяет исправить измерение " +
                        "без полной очистки."
            )

            GuideCard(
                title = "«Очистить»",
                description = "Удаляет все точки текущего измерения и позволяет начать заново."
            )

            GuideCard(
                title = "«История»",
                description = "Открывает экран истории измерений. " +
                        "Ниже в руководстве представлено подробное описание экрана истории."
            )

            GuideCard(
                title = "«Помощь»",
                description = "Открывает данное руководство пользователя. Для закрытия нажмите кнопку назад."
            )

            GuideCard(
                title = "«Сохранить измерение»",
                description = "Предназначена для сохранения результата измерения в историю."
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ЭКРАН ИСТОРИИ
            SectionTitle("Экран истории")
            GuideCard(
                title = "«Что позволяет делать»",
                description = "В будущем будет описание и функционал этого экрана"
            )

            // РЕКОМЕНДАЦИИ
            SectionTitle("Рекомендации")
            GuideCard(
                title = "Для более точных измерений",
                description = "• Используйте хорошее освещение.\n" +
                        "• Перемещайте смартфон плавно.\n" +
                        "• Избегайте однотонных и отражающих " +
                        "поверхностей.\n" +
                        "• Для более точных результатов советуется  " +
                        "производить измерения на расстояние не более 1–2 метров.\n" +
                        "• Учитывайте, что измерения могут иметь погрешность."
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Авторы
            SectionTitle("Об авторах")

            GuideCard(
                title = "Разработчики приложения",
                description = "Викульев Вадим Александрович\n" +
                        "Соловьев Эдуард Александрович\n\n" +
                        "Группа: 1ПИб-02-4оп-23\n" +
                        "Череповецкий государственный университет\n" +
                        "2026 год"
            )

        }
    }
}

//Заголовок раздела руководства
@Composable
private fun SectionTitle(
    title: String
) {
    Text(
        text = title,
        fontSize = 21.sp,
        fontWeight = FontWeight.SemiBold,
        color = Graphite
    )
}

//Универсальная информационная карточка руководства
@Composable
private fun GuideCard(
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LightSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Graphite
            )

            Text(
                text = description,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                color = SecondaryText
            )
        }
    }
}
