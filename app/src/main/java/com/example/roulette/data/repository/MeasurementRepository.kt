
package com.example.roulette.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.roulette.data.local.MeasurementDao
import com.example.roulette.data.local.MeasurementEntity
import com.example.roulette.data.storage.ImageStorage
import com.example.roulette.domain.model.SavedMeasurement
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Управляет сохранёнными AR-измерениями.
 *
 * Объединяет работу с Room и файловым хранилищем.
 */
class MeasurementRepository(
    private val dao: MeasurementDao,
    private val imageStorage: ImageStorage
) {

    // Получить список всех сохранённых измерений
    fun getAllMeasurements(): Flow<List<SavedMeasurement>> {
        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Получить конкретное измерение
    suspend fun getMeasurement(id: Long): SavedMeasurement? {
        return dao.getById(id)?.toDomain()
    }

    // Сохранить новое измерение вместе с фотографией
    suspend fun saveMeasurement(
        bitmap: Bitmap,
        distanceMm: Int,
        name: String = "Измерение"
    ): Long {

        require(distanceMm > 0) {
            "Расстояние должно быть положительным"
        }

        val measurementName = name.trim().ifBlank {
            "Измерение"
        }

        // Сначала сохраняем фотографию
        val fileName = imageStorage.save(bitmap)

        try {
            // Затем создаём запись в Room
            val entity = MeasurementEntity(
                name = measurementName,
                distanceMm = distanceMm,
                createdAtMillis = System.currentTimeMillis(),
                imageFileName = fileName
            )


            val measurementId = dao.insert(entity)

    // После успешного сохранения в Room
    // дополнительно сохраняем фотографию в галерею.
            try {
                val gallerySaved = imageStorage.saveToGallery(bitmap)

                if (!gallerySaved) {
                    Log.w(
                        "MeasurementRepository",
                        "Не удалось сохранить фотографию в галерею"
                    )
                }
            } catch (error: Exception) {
                Log.w(
                    "MeasurementRepository",
                    "Ошибка сохранения фотографии в галерею",
                    error
                )
            }

            return measurementId


        } catch (error: Exception) {

            // Если запись в БД не удалась,
            // удаляем созданную фотографию.
            // NonCancellable позволяет выполнить очистку
            // даже при отмене корутины.
            withContext(NonCancellable) {
                try {
                    imageStorage.delete(fileName)
                } catch (cleanupError: Exception) {
                    error.addSuppressed(cleanupError)
                }
            }

            throw error
        }
    }

    // Переименовать измерение
    suspend fun renameMeasurement(
        id: Long,
        newName: String
    ) {
        val name = newName.trim()

        require(name.isNotEmpty()) {
            "Название не может быть пустым"
        }

        check(dao.rename(id, name) == 1) {
            "Измерение не найдено"
        }
    }

    // Удалить измерение
    suspend fun deleteMeasurement(id: Long) {

        val measurement = dao.getById(id) ?: return

        // Удаляем запись из базы данных
        val deleted = dao.deleteById(id)

        if (deleted == 0) return

        // Затем удаляем фотографию
        try {
            val imageDeleted = imageStorage.delete(
                measurement.imageFileName
            )

            if (!imageDeleted) {
                Log.w(
                    "MeasurementRepository",
                    "Не удалось удалить фотографию измерения $id"
                )
            }
        } catch (error: Exception) {
            Log.w(
                "MeasurementRepository",
                "Ошибка удаления фотографии $id",
                error
            )
        }
    }

    // Загрузить фотографию измерения
    suspend fun loadMeasurementImage(id: Long, maxSizePx: Int? = null): Bitmap? {

        val measurement = dao.getById(id) ?: return null

        return imageStorage.load(
            measurement.imageFileName,
            maxSizePx
        )
    }

    // Преобразование Entity в доменную модель
    private fun MeasurementEntity.toDomain(): SavedMeasurement {
        return SavedMeasurement(
            id = id,
            name = name,
            distanceMm = distanceMm,
            createdAtMillis = createdAtMillis,
            imageFileName = imageFileName
        )
    }
}
