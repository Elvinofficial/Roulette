
package com.example.roulette.ui.measurement

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

/**
 * Добавляет подпись расстояния на готовый AR-снимок.
 */
object MeasurementSnapshotRenderer {

    fun drawDistanceLabel(
        bitmap: Bitmap,
        distanceMm: Int,
        labelX: Float,
        labelY: Float,
        sceneWidth: Int,
        sceneHeight: Int,
        density: Float,
        scaledDensity: Float
    ): Bitmap {

        require(sceneWidth > 0 && sceneHeight > 0)

        val canvas = Canvas(bitmap)

        // Соотношение размеров AR-сцены и фотографии
        val scaleX = bitmap.width.toFloat() / sceneWidth
        val scaleY = bitmap.height.toFloat() / sceneHeight

        val text = "$distanceMm мм"

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 16f * scaledDensity * scaleY
            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )
        }

        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(221, 32, 32, 32)
        }

        val paddingX = 12f * density * scaleX
        val paddingY = 6f * density * scaleY
        val gap = 12f * density * scaleY

        val metrics = textPaint.fontMetrics

        val labelWidth =
            textPaint.measureText(text) + paddingX * 2

        val labelHeight =
            metrics.descent - metrics.ascent + paddingY * 2

        // Координаты середины AR-линии на фотографии
        val centerX = labelX * scaleX
        val centerY = labelY * scaleY

        // Размещаем подпись над серединой линии
        val left = (centerX - labelWidth / 2f)
            .coerceIn(0f, maxOf(0f, bitmap.width - labelWidth))

        val top = (centerY - gap - labelHeight)
            .coerceIn(0f, maxOf(0f, bitmap.height - labelHeight))

        val radius = 10f * density

        // Тёмная подложка
        canvas.drawRoundRect(
            left,
            top,
            left + labelWidth,
            top + labelHeight,
            radius,
            radius,
            backgroundPaint
        )

        // Белая надпись расстояния
        canvas.drawText(
            text,
            left + paddingX,
            top + paddingY - metrics.ascent,
            textPaint
        )

        return bitmap
    }
}
