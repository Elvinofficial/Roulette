
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Save
import com.example.roulette.config.AppConfig
import com.example.roulette.ui.components.RouletteButton
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Button
import kotlinx.coroutines.flow.collect

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
    val uiState by viewModel.uiState.collectAsState()

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


    // Готовность ARCore к установке точки
    var isPlacementReady by remember {
        mutableStateOf(false)
    }

    // Команда получения AR-снимка
    var captureRequest by remember {
        mutableIntStateOf(0)
    }

// Фотография для предварительного просмотра
    var previewBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    // Расстояние на момент создания снимка
    var captureDistanceMm by remember {
        mutableStateOf<Int?>(null)
    }

// Расстояние, соответствующее снимку в окне просмотра
    var previewDistanceMm by remember {
        mutableStateOf<Int?>(null)
    }

// Выполняется ли захват изображения
    var isCapturing by remember {
        mutableStateOf(false)
    }

    val canSave = uiState.pointCount == 2 &&
            (uiState.distanceMm ?: 0) > 0

    LaunchedEffect(viewModel) {
        viewModel.saveEvents.collect { event ->

            when (event) {

                is SaveMeasurementEvent.Success -> {

                    previewBitmap = null
                    previewDistanceMm = null

                    Toast.makeText(
                        context,
                        "Измерение сохранено!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is SaveMeasurementEvent.Error -> {

                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
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
                icon = Icons.Default.Info,
                onClick = onGuideClick,
                modifier = Modifier.weight(1f)
            )

            // Переход к истории измерений
            RouletteButton(
                text = "История",
                icon = Icons.Default.History,
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
                    onPointCountChanged = viewModel::onPointCountChanged,
                    onDistanceChanged = viewModel::onDistanceChanged,
                    captureRequest = captureRequest,

                    onSnapshotCaptured = { bitmap ->
                        isCapturing = false

                        previewDistanceMm = captureDistanceMm
                        captureDistanceMm = null

                        previewBitmap = bitmap
                    },

                    onSnapshotFailed = { message ->
                        isCapturing = false
                        captureDistanceMm = null

                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    },
                )
            }
                // Прицел
            CenterReticle(
                isReady = AppConfig.AR_ENABLED && isPlacementReady
            )
        }

        // СЧЁТЧИК УСТАНОВЛЕННЫХ ТОЧЕК
        Text(
            text = "Точек: ${uiState.pointCount}/2",
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
                icon = Icons.Default.Delete,
                onClick = {
                    clearPointsRequest++
                },

                // Очистка доступна только при наличии точек
                enabled = uiState.pointCount > 0,
                modifier = Modifier.weight(1f)
            )

            // Удаление последней установленной точки
            RouletteButton(
                text = "Отменить",
                icon = Icons.Default.Undo,
                onClick = {
                    undoPointRequest++
                },

                // Отмена доступна только при наличии точек
                enabled = uiState.pointCount > 0,
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

                enabled = AppConfig.AR_ENABLED &&  hasCameraPermission && uiState.pointCount < 2,

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
            icon = Icons.Default.Save,

            onClick = {
                captureDistanceMm = uiState.distanceMm
                isCapturing = true
                captureRequest++
            },

            enabled = canSave &&
                    !isCapturing &&
                    !uiState.isSaving,

            primary = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
    // Предварительный просмотр AR-снимка
    previewBitmap?.let { bitmap ->

        Dialog(
            onDismissRequest = {
                if (!uiState.isSaving) {
                    previewBitmap = null
                    previewDistanceMm = null
                }
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Снимок AR-измерения",

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 520.dp),

                    contentScale = ContentScale.Fit
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Закрыть предварительный просмотр
                    TextButton(
                        onClick = {
                            previewBitmap = null
                            previewDistanceMm = null
                        },
                        enabled = !uiState.isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Закрыть")
                    }

                    // Сохранить фотографию и расстояние
                    Button(
                        onClick = {
                            val distance = previewDistanceMm

                            if (distance != null) {
                                viewModel.saveMeasurement(
                                    bitmap = bitmap,
                                    distanceMm = distance
                                )
                            }
                        },
                        enabled = !uiState.isSaving &&
                                previewDistanceMm != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (uiState.isSaving) {
                                "Сохраняем..."
                            } else {
                                "Сохранить"
                            }
                        )
                    }
                }

            }
        }
    }
}
