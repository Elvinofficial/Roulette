
package com.example.roulette.ui.measurement

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roulette.data.repository.MeasurementRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MeasurementUiState(
    val pointCount: Int = 0,
    val distanceMm: Int? = null,
    val isSaving: Boolean = false
)

// События сохранения для интерфейса
sealed interface SaveMeasurementEvent {

    data class Success(
        val id: Long
    ) : SaveMeasurementEvent

    data class Error(
        val message: String
    ) : SaveMeasurementEvent
}

class MeasurementViewModel(
    private val repository: MeasurementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MeasurementUiState()
    )

    val uiState: StateFlow<MeasurementUiState> =
        _uiState.asStateFlow()

    private val _saveEvents =
        Channel<SaveMeasurementEvent>(Channel.BUFFERED)

    val saveEvents = _saveEvents.receiveAsFlow()

    val savedMeasurements = repository.getAllMeasurements()

    suspend fun loadSavedMeasurementImage(id: Long, maxSizePx: Int? = null): Bitmap? =
        repository.loadMeasurementImage(id, maxSizePx)

    suspend fun renameSavedMeasurement(id: Long, name: String) {
        repository.renameMeasurement(id, name)
    }

    suspend fun deleteSavedMeasurement(id: Long) {
        repository.deleteMeasurement(id)
    }

    fun onPointCountChanged(count: Int) {
        _uiState.update { current ->
            current.copy(
                pointCount = count,
                distanceMm = if (count == 2) {
                    current.distanceMm
                } else {
                    null
                }
            )
        }
    }

    fun onDistanceChanged(distanceMm: Int?) {
        _uiState.update {
            it.copy(distanceMm = distanceMm)
        }
    }

    // Сохранение готового AR-снимка
    fun saveMeasurement(
        bitmap: Bitmap,
        distanceMm: Int,
        name: String
    ) {
        // Защита от повторного нажатия
        if (_uiState.value.isSaving) return

        _uiState.update {
            it.copy(isSaving = true)
        }

        viewModelScope.launch {
            try {
                // Передаём данные в Repository
                val id = repository.saveMeasurement(
                    bitmap = bitmap,
                    distanceMm = distanceMm,
                    name = name
                )

                // Уведомляем интерфейс об успехе
                _saveEvents.send(
                    SaveMeasurementEvent.Success(id)
                )

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {

                _saveEvents.send(
                    SaveMeasurementEvent.Error(
                        error.message
                            ?: "Не удалось сохранить измерение"
                    )
                )

            } finally {
                _uiState.update {
                    it.copy(isSaving = false)
                }
            }
        }
    }
}
