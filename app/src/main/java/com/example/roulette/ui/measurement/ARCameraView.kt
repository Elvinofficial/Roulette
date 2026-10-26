package com.example.roulette.ui.measurement

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.ar.core.Point
import com.google.ar.core.HitResult
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun ARCameraView(
    modifier: Modifier = Modifier,
    placePointRequest: Int = 0,
    onPointPlaced: (Float, Float, Float) -> Unit = { _, _, _ -> },
    onPlacementReadyChanged: (Boolean) -> Unit = {}
) {
    val lastHandledRequest = remember { intArrayOf(placePointRequest) }

    var sceneWidth by remember { mutableIntStateOf(0) }
    var sceneHeight by remember { mutableIntStateOf(0) }
    val currentReadyCallback = rememberUpdatedState(onPlacementReadyChanged)
    val currentPointCallback = rememberUpdatedState(onPointPlaced)

    val lastReadyState = remember { booleanArrayOf(false) }

    ARScene(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                sceneWidth = size.width
                sceneHeight = size.height
            },

        planeRenderer = false,

        onSessionUpdated = { _, frame ->

            val tracking = frame.camera.trackingState == TrackingState.TRACKING

            val hits = if (tracking && sceneWidth > 0 && sceneHeight > 0) {
                frame.hitTest(
                    sceneWidth / 2f,
                    sceneHeight / 2f
                )
            } else {
                emptyList()
            }

            // Сначала предпочитаем плоскость
            val planeHit = hits.firstOrNull { result ->
                val plane = result.trackable as? Plane

                plane != null &&
                        plane.trackingState == TrackingState.TRACKING &&
                        plane.isPoseInPolygon(result.hitPose)
            }

            // Если плоскости нет — ищем характерную точку
            val pointHit = hits.firstOrNull { result ->
                val point = result.trackable as? Point

                point != null &&
                        point.trackingState == TrackingState.TRACKING &&
                        point.orientationMode ==
                        Point.OrientationMode.ESTIMATED_SURFACE_NORMAL
            }

            val hit: HitResult? = planeHit ?: pointHit
            val isReady = hit != null

            // Уведомляем интерфейс только при изменении состояния
            if (isReady != lastReadyState[0]) {
                lastReadyState[0] = isReady
                currentReadyCallback.value(isReady)
            }

            if (placePointRequest != lastHandledRequest[0]) {
                lastHandledRequest[0] = placePointRequest

                if (hit != null) {
                    val pose = hit.hitPose

                    currentPointCallback.value(
                        pose.tx(),
                        pose.ty(),
                        pose.tz()
                    )
                } else {
                    android.util.Log.d(
                        "RouletteAR",
                        "Поверхность под прицелом не найдена"
                    )
                }
            }
        }
    )
}