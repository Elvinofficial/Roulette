
package com.example.roulette.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * Отвечает за хранение фотографий измерений
 * во внутренней памяти приложения.
 */
class ImageStorage(context: Context) {

    private val imageDirectory = File(
        context.applicationContext.filesDir,
        "measurements"
    )

    /**
     * Сохраняет фотографию и возвращает имя файла.
     */
    suspend fun save(bitmap: Bitmap): String =
        withContext(Dispatchers.IO) {

            if (!imageDirectory.exists() &&
                !imageDirectory.mkdirs()
            ) {
                throw IOException(
                    "Не удалось создать папку измерений"
                )
            }

            val fileName =
                "measurement_${UUID.randomUUID()}.jpg"

            val file = File(imageDirectory, fileName)

            try {
                file.outputStream().use { stream ->

                    val success = bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        90,
                        stream
                    )

                    if (!success) {
                        throw IOException(
                            "Не удалось сохранить изображение"
                        )
                    }
                }

                fileName

            } catch (error: Exception) {

                // Удаляем повреждённый или неполный файл
                file.delete()

                throw error
            }
        }

    /**
     * Загружает фотографию по имени файла.
     */
    suspend fun load(fileName: String): Bitmap? =
        withContext(Dispatchers.IO) {

            val file = getImageFile(fileName)

            if (!file.exists()) {
                return@withContext null
            }

            BitmapFactory.decodeFile(file.absolutePath)
        }

    /**
     * Удаляет фотографию.
     */
    suspend fun delete(fileName: String): Boolean =
        withContext(Dispatchers.IO) {

            val file = getImageFile(fileName)

            !file.exists() || file.delete()
        }

    private fun getImageFile(fileName: String): File {
        require(
            fileName.startsWith("measurement_") &&
                    fileName.endsWith(".jpg") &&
                    fileName == File(fileName).name &&
                    '\\' !in fileName
        ) {
            "Некорректное имя файла"
        }

        return File(imageDirectory, fileName)
    }
}
