
package com.example.roulette.ui.measurement

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

import com.example.roulette.config.AppConfig
import com.example.roulette.ui.components.RouletteButton

// Цвет вспомогательного текста интерфейса
private val SecondaryText = Color(0xFF666666)

//  Основной экран измерений приложения
@Composable
fun MeasurementScreen(
    viewModel: MeasurementViewModel,
    onHistoryClick: () -> Unit,
    onGuideClick: () -> Unit
) {
    val context = LocalContext.current

    // Проверка, предоставлено ли приложению разрешение камеры
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Обработчик системного запроса разрешения
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    // Запрашивает разрешение только при включённом AR.
    LaunchedEffect(AppConfig.AR_ENABLED) {
        if (AppConfig.AR_ENABLED && !hasCameraPermission) {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    // Запрос на установку новой точки
    var placePointRequest by remember {
        mutableIntStateOf(0)
    }

    // Запрос на удаление последней точки
    var undoPointRequest by remember {
        mutableIntStateOf(0)
    }

    // Запрос на удаление всех установленных точек
    var clearPointsRequest by remember {
        mutableIntStateOf(0)
    }

    // Фактическое количество установленных AR-точек
    var arPointCount by remember {
        mutableIntStateOf(0)
    }

    // Готовность ARCore к установке точки
    var isPlacementReady by remember {
        mutableStateOf(false)
    }

 // ОСНОВНОЙ КОНТЕЙНЕР ЭКРАНА
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

            // Учитывает системные панели Android
            .safeDrawingPadding()

            // Внутренние отступы интерфейса
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),

        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ВЕРХНЯЯ ПАНЕЛЬ НАВИГАЦИИ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Открытие руководства пользователя
            RouletteButton(
                text = "Помощь",
                onClick = onGuideClick,
                modifier = Modifier.weight(1f)
            )

            // Переход к истории измерений
            RouletteButton(
                text = "История",
                onClick = onHistoryClick,
                modifier = Modifier.weight(1f)
            )
        }

        // ОБЛАСТЬ AR-КАМЕРЫ
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (AppConfig.AR_ENABLED) {
                        Color.Black
                    } else {
                        Color(0xFFF0F0F0)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {

            // Создаёт AR сцену только при включённом AR
            // и наличии разрешения на использование камеры
            if (AppConfig.AR_ENABLED && hasCameraPermission) {

                ARCameraView(
                    modifier = Modifier.fillMaxSize(),

                    // Передаёт команды управления точками
                    placePointRequest = placePointRequest,
                    undoPointRequest = undoPointRequest,
                    clearPointsRequest = clearPointsRequest,
                    // Получает информацию о готовности ARCore к установке новой точки
                    onPlacementReadyChanged = { ready ->
                        isPlacementReady = ready
                    },
                    // Получает координаты успешно установленной точки в пространстве
                    onPointPlaced = { x, y, z ->
                        Log.d(
                            "RouletteAR",
                            "AR point: x=$x, y=$y, z=$z"
                        )
                    },
                    // Синхронизирует отображаемое количество точек с фактическим состоянием AR-сцены
                    onPointCountChanged = { count ->
                        arPointCount = count
                    }
                )
            }
                // Прицел
            CenterReticle(
                isReady = AppConfig.AR_ENABLED && isPlacementReady
            )
        }

        // СЧЁТЧИК УСТАНОВЛЕННЫХ ТОЧЕК
        Text(
            text = "Точек: $arPointCount/2",
            fontSize = 16.sp,
            color = SecondaryText
        )

        // УПРАВЛЕНИЕ УСТАНОВЛЕННЫМИ ТОЧКАМИ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Удаление всех точек текущего измерения
            RouletteButton(
                text = "Очистить",
                onClick = {
                    clearPointsRequest++
                },

                // Очистка доступна только при наличии точек
                enabled = arPointCount > 0,
                modifier = Modifier.weight(1f)
            )

            // Удаление последней установленной точки
            RouletteButton(
                text = "Отменить",
                onClick = {
                    undoPointRequest++
                },

                // Отмена доступна только при наличии точек
                enabled = arPointCount > 0,
                modifier = Modifier.weight(1f)
            )
        }


        // КНОПКА УСТАНОВКИ НОВОЙ ТОЧКИ
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            RouletteButton(
                text = "+",

                onClick = {
                    // Отправляет запрос на установку точки
                    placePointRequest++

                    Log.d(
                        "RouletteAR",
                        "Кнопка нажата: $placePointRequest"
                    )
                },

                enabled = AppConfig.AR_ENABLED &&  hasCameraPermission && arPointCount < 2,

                primary = true,

                // Размер основной кнопки измерения
                modifier = Modifier.width(68.dp),
                height = 68.dp,
                fontSize = 32.sp
            )
        }

        // СОХРАНЕНИЕ ИЗМЕРЕНИЯ
        RouletteButton(
            text = "Сохранить измерение",
            onClick = {
                // TODO: Реализовать сохранение измерения
            },
            enabled = false,
            primary = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
