
package com.example.roulette.ui.measurement

import android.opengl.Matrix
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Point
import com.google.ar.core.TrackingState
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.rememberEngine
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun ARCameraView(
    modifier: Modifier = Modifier,
    placePointRequest: Int = 0,
    undoPointRequest: Int = 0,
    clearPointsRequest: Int = 0,
    onPointPlaced: (Float, Float, Float) -> Unit = { _, _, _ -> },
    onPlacementReadyChanged: (Boolean) -> Unit = {},
    onPointRemoved: () -> Unit = {},
    onAllPointsRemoved: () -> Unit = {},
    onDistanceChanged: (Int?) -> Unit = {},
    onPointCountChanged: (Int) -> Unit = {}
) {
    val engine = rememberEngine()
    val context = LocalContext.current

    var sceneWidth by remember { mutableIntStateOf(0) }
    var sceneHeight by remember { mutableIntStateOf(0) }

    val lastPlaceRequest = remember {
        intArrayOf(placePointRequest)
    }
    val lastUndoRequest = remember {
        intArrayOf(undoPointRequest)
    }
    val lastClearRequest = remember {
        intArrayOf(clearPointsRequest)
    }

    val lastReadyState = remember {
        booleanArrayOf(false)
    }

    val onPlacedCallback = rememberUpdatedState(onPointPlaced)
    val onReadyCallback =
        rememberUpdatedState(onPlacementReadyChanged)
    val onRemovedCallback = rememberUpdatedState(onPointRemoved)
    val onClearedCallback =
        rememberUpdatedState(onAllPointsRemoved)
    val onDistanceCallback =
        rememberUpdatedState(onDistanceChanged)
    val onCountCallback =
        rememberUpdatedState(onPointCountChanged)

    val materialLoader = remember(engine, context) {
        MaterialLoader(
            engine = engine,
            context = context
        )
    }

    val yellowMaterial = remember(materialLoader) {
        materialLoader.createColorInstance(
            color = Color.Yellow,
            metallic = 0f,
            roughness = 1f,
            reflectance = 0.5f
        )
    }

    // Одно измерение = один AR-якорь
    val measurementAnchor = remember {
        arrayOfNulls<Anchor>(1)
    }

    val measurementNodes = remember {
        mutableStateListOf<AnchorNode>()
    }

    // Ссылки на геометрию для корректного удаления
    val firstSphere = remember {
        arrayOfNulls<SphereNode>(1)
    }
    val secondSphere = remember {
        arrayOfNulls<SphereNode>(1)
    }
    val lineNode = remember {
        arrayOfNulls<CylinderNode>(1)
    }

    // 0, 1 или 2 установленные точки
    val pointCount = remember {
        intArrayOf(0)
    }

    // Локальная середина измерения для подписи
    val localMidpoint = remember {
        arrayOfNulls<FloatArray>(1)
    }

    // Значение расстояния и позиция подписи на экране
    var distanceMm by remember {
        mutableStateOf<Int?>(null)
    }

    var labelPosition by remember {
        mutableStateOf<Offset?>(null)
    }

    var labelSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val labelGapPx = with(LocalDensity.current) {
        12.dp.toPx()
    }

    // Переиспользуем матрицы, не создавая их каждый кадр
    val viewMatrix = remember { FloatArray(16) }
    val projectionMatrix = remember { FloatArray(16) }
    val worldPoint = remember { FloatArray(4) }
    val cameraPoint = remember { FloatArray(4) }
    val clipPoint = remember { FloatArray(4) }

    // Удаляем вторую точку и соединительную линию
    fun removeSecondPoint() {
        val root = measurementNodes.firstOrNull()

        lineNode[0]?.let { node ->
            root?.removeChildNode(node)
            node.destroy()
            lineNode[0] = null
        }

        secondSphere[0]?.let { node ->
            root?.removeChildNode(node)
            node.destroy()
            secondSphere[0] = null
        }

        localMidpoint[0] = null
        distanceMm = null
        labelPosition = null

        pointCount[0] = if (root != null) 1 else 0
    }

    // Удаляем всё измерение
    fun removeAllPoints() {
        removeSecondPoint()

        val root = measurementNodes.firstOrNull()

        firstSphere[0]?.let { node ->
            root?.removeChildNode(node)
            node.destroy()
            firstSphere[0] = null
        }

        measurementNodes.clear()

        if (root != null) {
            // AnchorNode.destroy() также освобождает AR-якорь
            root.destroy()
        } else {
            measurementAnchor[0]?.detach()
        }

        measurementAnchor[0] = null
        pointCount[0] = 0
        distanceMm = null
        labelPosition = null
    }

    DisposableEffect(Unit) {
        onDispose {
            removeAllPoints()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        ARScene(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    sceneWidth = size.width
                    sceneHeight = size.height
                },

            // Обязательно тот же Engine
            engine = engine,

            planeRenderer = false,

            childNodes = measurementNodes.toList(),

            onSessionUpdated = { _, frame ->

                val tracking =
                    frame.camera.trackingState ==
                            TrackingState.TRACKING

                val hits = if (
                    tracking &&
                    sceneWidth > 0 &&
                    sceneHeight > 0 &&
                    pointCount[0] < 2
                ) {
                    frame.hitTest(
                        sceneWidth / 2f,
                        sceneHeight / 2f
                    )
                } else {
                    emptyList()
                }

                val planeHit = hits.firstOrNull { result ->
                    val plane = result.trackable as? Plane

                    plane != null &&
                            plane.trackingState == TrackingState.TRACKING &&
                            plane.isPoseInPolygon(result.hitPose)
                }

                val pointHit = hits.firstOrNull { result ->
                    val point = result.trackable as? Point

                    point != null &&
                            point.trackingState == TrackingState.TRACKING &&
                            point.orientationMode ==
                            Point.OrientationMode.ESTIMATED_SURFACE_NORMAL
                }

                val hit: HitResult? = planeHit ?: pointHit
                val isReady = hit != null

                if (isReady != lastReadyState[0]) {
                    lastReadyState[0] = isReady
                    onReadyCallback.value(isReady)
                }

                // 1. Полная очистка
                if (clearPointsRequest != lastClearRequest[0]) {
                    lastClearRequest[0] = clearPointsRequest

                    removeAllPoints()

                    onClearedCallback.value()
                    onDistanceCallback.value(null)
                    onCountCallback.value(0)

                    Log.d("RouletteAR", "Измерение очищено")
                }

                // 2. Отмена последней точки
                if (undoPointRequest != lastUndoRequest[0]) {
                    lastUndoRequest[0] = undoPointRequest

                    when (pointCount[0]) {
                        2 -> {
                            removeSecondPoint()

                            onRemovedCallback.value()
                            onDistanceCallback.value(null)
                            onCountCallback.value(1)

                            Log.d(
                                "RouletteAR",
                                "Вторая точка и линия удалены"
                            )
                        }

                        1 -> {
                            removeAllPoints()

                            onRemovedCallback.value()
                            onDistanceCallback.value(null)
                            onCountCallback.value(0)

                            Log.d(
                                "RouletteAR",
                                "Первая точка удалена"
                            )
                        }
                    }
                }

                // 3. Установка точки
                if (placePointRequest != lastPlaceRequest[0]) {
                    lastPlaceRequest[0] = placePointRequest

                    if (hit != null) {
                        when (pointCount[0]) {

                            // ПЕРВАЯ ТОЧКА
                            0 -> {
                                val anchor = hit.createAnchor()

                                val root = AnchorNode(
                                    engine = engine,
                                    anchor = anchor
                                ).apply {
                                    isPositionEditable = false
                                }

                                val sphere = SphereNode(
                                    engine = engine,
                                    radius = 0.0075f,
                                    materialInstance = yellowMaterial
                                )

                                root.addChildNode(sphere)

                                measurementAnchor[0] = anchor
                                firstSphere[0] = sphere
                                measurementNodes.add(root)
                                pointCount[0] = 1

                                val pose = anchor.pose

                                onPlacedCallback.value(
                                    pose.tx(),
                                    pose.ty(),
                                    pose.tz()
                                )
                                onCountCallback.value(1)

                                Log.d(
                                    "RouletteAR",
                                    "Первая точка установлена"
                                )
                            }

                            // ВТОРАЯ ТОЧКА
                            1 -> {
                                val anchor = measurementAnchor[0]
                                val root = measurementNodes.firstOrNull()

                                if (
                                    anchor != null &&
                                    root != null &&
                                    anchor.trackingState ==
                                    TrackingState.TRACKING
                                ) {
                                    val local = anchor.pose
                                        .inverse()
                                        .transformPoint(
                                            hit.hitPose.translation
                                        )

                                    val dx = local[0]
                                    val dy = local[1]
                                    val dz = local[2]

                                    val distance = sqrt(
                                        dx * dx +
                                                dy * dy +
                                                dz * dz
                                    )

                                    if (distance > 0.001f) {
                                        val sphere = SphereNode(
                                            engine = engine,
                                            radius = 0.0075f,
                                            materialInstance = yellowMaterial
                                        ).apply {
                                            position = Position(
                                                dx, dy, dz
                                            )
                                        }

                                        val line = CylinderNode(
                                            engine = engine,
                                            radius = 0.0025f,
                                            height = 1f,
                                            materialInstance = yellowMaterial
                                        )

                                        line.transform(
                                            position = Position(
                                                dx / 2f,
                                                dy / 2f,
                                                dz / 2f
                                            ),
                                            quaternion = rotationFromY(
                                                dx, dy, dz, distance
                                            ),
                                            scale = Scale(
                                                1f,
                                                distance,
                                                1f
                                            ),
                                            smooth = false
                                        )

                                        root.addChildNode(sphere)
                                        root.addChildNode(line)

                                        secondSphere[0] = sphere
                                        lineNode[0] = line

                                        localMidpoint[0] = floatArrayOf(
                                            dx / 2f,
                                            dy / 2f,
                                            dz / 2f
                                        )

                                        pointCount[0] = 2

                                        // ARCore использует метры:
                                        // переводим в миллиметры
                                        val mm = (distance * 1000f)
                                            .roundToInt()

                                        distanceMm = mm

                                        val world = anchor.pose
                                            .transformPoint(local)

                                        onPlacedCallback.value(
                                            world[0],
                                            world[1],
                                            world[2]
                                        )

                                        onDistanceCallback.value(mm)
                                        onCountCallback.value(2)

                                        Log.d(
                                            "RouletteAR",
                                            "Измерено: $mm мм"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Перемещаем подпись вслед за линией
                val anchor = measurementAnchor[0]
                val midpoint = localMidpoint[0]

                if (
                    tracking &&
                    pointCount[0] == 2 &&
                    anchor != null &&
                    midpoint != null &&
                    anchor.trackingState ==
                    TrackingState.TRACKING &&
                    sceneWidth > 0 &&
                    sceneHeight > 0
                ) {
                    // Середина линии в мировых координатах
                    val world = anchor.pose.transformPoint(
                        midpoint
                    )

                    worldPoint[0] = world[0]
                    worldPoint[1] = world[1]
                    worldPoint[2] = world[2]
                    worldPoint[3] = 1f

                    frame.camera.getViewMatrix(
                        viewMatrix, 0
                    )

                    frame.camera.getProjectionMatrix(
                        projectionMatrix,
                        0,
                        0.01f,
                        100f
                    )

                    // World -> Camera
                    Matrix.multiplyMV(
                        cameraPoint, 0,
                        viewMatrix, 0,
                        worldPoint, 0
                    )

                    // Camera -> Clip
                    Matrix.multiplyMV(
                        clipPoint, 0,
                        projectionMatrix, 0,
                        cameraPoint, 0
                    )

                    val w = clipPoint[3]

                    if (w > 0f) {
                        val ndcX = clipPoint[0] / w
                        val ndcY = clipPoint[1] / w

                        if (
                            abs(ndcX) <= 1f &&
                            abs(ndcY) <= 1f
                        ) {
                            val x = (ndcX + 1f) *
                                    0.5f * sceneWidth

                            val y = (1f - ndcY) *
                                    0.5f * sceneHeight

                            val newPosition = Offset(x, y)
                            val oldPosition = labelPosition

                            // Не обновляем Compose ради
                            // совсем незначительных изменений
                            if (
                                oldPosition == null ||
                                abs(oldPosition.x - x) > 1f ||
                                abs(oldPosition.y - y) > 1f
                            ) {
                                labelPosition = newPosition
                            }
                        } else {
                            labelPosition = null
                        }
                    } else {
                        labelPosition = null
                    }
                } else {
                    labelPosition = null
                }
            }
        )

        // Подпись поверх изображения камеры
        val mm = distanceMm
        val screenPosition = labelPosition

        if (mm != null && screenPosition != null) {
            Text(
                text = "$mm мм",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset {
                        IntOffset(
                            x = (
                                    screenPosition.x -
                                            labelSize.width / 2f
                                    ).roundToInt(),
                            y = (
                                    screenPosition.y -
                                            labelSize.height -
                                            labelGapPx
                                    ).roundToInt()
                        )
                    }
                    .onSizeChanged {
                        labelSize = it
                    }
                    .background(
                        color = Color(0xDD202020),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
            )
        }
    }
}

/**
 * Поворот цилиндра от оси Y
 * к направлению между двумя точками.
 */
private fun rotationFromY(
    dx: Float,
    dy: Float,
    dz: Float,
    distance: Float
): Quaternion {
    val nx = dx / distance
    val ny = dy / distance
    val nz = dz / distance

    return when {
        ny > 0.99999f -> {
            Quaternion(0f, 0f, 0f, 1f)
        }

        ny < -0.99999f -> {
            Quaternion(1f, 0f, 0f, 0f)
        }

        else -> {
            val s = sqrt(2f * (1f + ny))

            Quaternion(
                nz / s,
                0f,
                -nx / s,
                s / 2f
            )
        }
    }
}
