
package com.example.roulette.data.capture

import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Захватывает изображение AR-сцены.
 *
 * Компонент отвечает только за получение Bitmap.
 * Сохранением файла и работой с БД он не занимается.
 */
object ArSnapshotCapturer {

    suspend fun capture(surfaceView: SurfaceView): Bitmap {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            throw UnsupportedOperationException(
                "Захват через PixelCopy требует Android 8.0 или новее"
            )
        }

        require(surfaceView.width > 0 && surfaceView.height > 0) {
            "AR-сцена ещё не готова к захвату"
        }

        return captureWithPixelCopy(surfaceView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun captureWithPixelCopy(
        surfaceView: SurfaceView
    ): Bitmap = suspendCancellableCoroutine { continuation ->

        val bitmap = Bitmap.createBitmap(
            surfaceView.width,
            surfaceView.height,
            Bitmap.Config.ARGB_8888
        )

        try {
            PixelCopy.request(
                surfaceView,
                bitmap,
                { result ->
                    if (!continuation.isActive) {
                        bitmap.recycle()
                    } else if (result == PixelCopy.SUCCESS) {
                        continuation.resume(bitmap)
                    } else {
                        bitmap.recycle()

                        continuation.resumeWithException(
                            IllegalStateException(
                                "Ошибка захвата AR-кадра: $result"
                            )
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } catch (error: Exception) {
            bitmap.recycle()

            if (continuation.isActive) {
                continuation.resumeWithException(error)
            }
        }
    }
}
