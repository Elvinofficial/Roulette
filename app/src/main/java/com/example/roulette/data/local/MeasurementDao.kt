
package com.example.roulette.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    // Сохранить новое измерение
    @Insert
    suspend fun insert(
        measurement: MeasurementEntity
    ): Long

    // Получить все измерения
    // Новые записи отображаются первыми
    @Query(
        "SELECT * FROM measurements " +
                "ORDER BY createdAtMillis DESC, id DESC"
    )
    fun getAll(): Flow<List<MeasurementEntity>>

    // Получить конкретное измерение
    @Query(
        "SELECT * FROM measurements WHERE id = :id LIMIT 1"
    )
    suspend fun getById(
        id: Long
    ): MeasurementEntity?

    // Переименовать запись
    @Query(
        "UPDATE measurements SET name = :newName " +
                "WHERE id = :id"
    )
    suspend fun rename(
        id: Long,
        newName: String
    ): Int

    // Удалить запись
    @Query(
        "DELETE FROM measurements WHERE id = :id"
    )
    suspend fun deleteById(
        id: Long
    ): Int
}
