
package com.example.roulette.ui.measurement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.mutableIntStateOf

private val Graphite = Color(0xFF242424)
private val LightGray = Color(0xFFF5F5F5)
private val SecondaryText = Color(0xFF858585)
private val BorderGray = Color(0xFFE8E8E8)

@Composable
fun MeasurementScreen(
    viewModel: MeasurementViewModel,
    onHistoryClick: () -> Unit
) {
    val points by viewModel.points.collectAsStateWithLifecycle()
    val distance = if (points.size == 2) viewModel.distance else null
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    var placePointRequest by remember {
        mutableIntStateOf(0)
    }
    var isPlacementReady by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Рулетка",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Graphite
                )

            }

            TextButton(
                onClick = onHistoryClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Graphite
                )
            ) {
                Text(
                    text = "История",
                    fontSize = 14.sp
                )
            }
        }

        // Поле измерения
        // Поле измерения с AR-камерой
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    LightGray,
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                ARCameraView(
                    modifier = Modifier.fillMaxSize(),
                    placePointRequest = placePointRequest,

                    onPlacementReadyChanged = { ready ->
                        isPlacementReady = ready
                    },

                    onPointPlaced = { x, y, z ->
                        android.util.Log.d(
                            "RouletteAR",
                            "AR point: x=$x, y=$y, z=$z"
                        )
                    }
                )

                CenterReticle(
                    isReady = isPlacementReady
                )
            } else {

                Text(
                    text = "Для работы AR необходимо разрешение камеры",
                    color = SecondaryText,
                    fontSize = 14.sp
                )

            }
        }

        // Счётчик точек
        Text(
            text = "Точек: ${points.size}/2",
            fontSize = 13.sp,
            color = SecondaryText
        )

        // Отмена и очистка
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = viewModel::undoPoint,
                enabled = points.isNotEmpty(),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Graphite
                )
            ) {
                Text("Отменить")
            }

            OutlinedButton(
                onClick = viewModel::clearPoints,
                enabled = points.isNotEmpty(),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Graphite
                )
            ) {
                Text("Очистить")
            }
        }

        // Основная кнопка добавления точки
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    placePointRequest++

                    android.util.Log.d(
                        "RouletteAR",
                        "Кнопка нажата: $placePointRequest"
                    )
                },
                //enabled = points.size < 2,
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Graphite,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }

        // Сохранение
        Button(
            onClick = { /* Будет позже */ },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Graphite
            )
        ) {
            Text("Сохранить измерение")
        }
    }
}
