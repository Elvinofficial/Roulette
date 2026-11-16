
package com.example.roulette.ui.measurement

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.filament.gltfio.MaterialProvider.MaterialKey
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberScene
import androidx.compose.ui.platform.LocalContext

@Composable
fun TestScreen(
    modifier: Modifier = Modifier
) {
    val engine = rememberEngine()
    val context = LocalContext.current
    val scene = rememberScene(engine)
    val materialLoader = rememberMaterialLoader(engine, context)

    // Тот же материал, что используется в ARCameraView
    val testMaterial = remember(materialLoader) {
        materialLoader.createColorInstance(
            color = Color.Green,
            metallic = 0f,
            roughness = 1f,
            reflectance = 0.5f
        )
    }

    val cameraNode = rememberCameraNode(engine) {
        position = Position(0f, 0f, 1.5f)
        lookAt(Position(0f, 0f, 0f))
    }

    // Первая точка
    val firstPoint = remember(engine, testMaterial) {
        SphereNode(
            engine = engine,
            radius = 0.025f,
            materialInstance = testMaterial
        ).apply {
            position = Position(-0.2f, 0f, 0f)
        }
    }

    // Вторая точка
    val secondPoint = remember(engine, testMaterial) {
        SphereNode(
            engine = engine,
            radius = 0.025f,
            materialInstance = testMaterial
        ).apply {
            position = Position(0.2f, 0f, 0f)
        }
    }

    // Соединительная линия
    val line = remember(engine, testMaterial) {
        CylinderNode(
            engine = engine,
            radius = 0.008f,
            height = 1f,
            materialInstance = testMaterial
        ).apply {
            transform(
                position = Position(0f, 0f, 0f),
                scale = Scale(1f, 0.4f, 1f),
                smooth = false
            )

            // Цилиндр вдоль Y, поворачиваем вдоль X
            rotation = io.github.sceneview.math.Rotation(
                0f, 0f, 90f
            )
        }
    }

    Scene(
        modifier = modifier.fillMaxSize(),
        engine = engine,
        scene = scene,
        materialLoader = materialLoader,
        cameraNode = cameraNode,
        childNodes = listOf(
            firstPoint,
            secondPoint,
            line
        ),

        onViewCreated = {
            scene.skybox = null

            renderer.clearOptions =
                com.google.android.filament.Renderer.ClearOptions().apply {
                    clear = true
                    clearColor = floatArrayOf(
                        1f, 0f, 0f, 1f
                    )
                }
        }
    )
}