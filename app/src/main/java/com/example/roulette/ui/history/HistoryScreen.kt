package com.example.roulette.ui.history

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.roulette.ui.components.RouletteButton
import com.example.roulette.ui.measurement.MeasurementViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Graphite = Color(0xFF242424)
private val SecondaryText = Color(0xFF626B73)
private val HistoryBackground = Color(0xFFF5F6F7)
private val CardBorder = Color(0xFFE2E5E8)
private val DeleteTint = Color(0xFF9E4141)
private val ThumbnailSize = 64.dp
private val LegacyAutomaticName =
    Regex("""Измерение \p{Nd}{2}\.\p{Nd}{2}\.\p{Nd}{4} \p{Nd}{2}:\p{Nd}{2}""")

@Composable
fun HistoryScreen(
    viewModel: MeasurementViewModel,
    onBackClick: () -> Unit
) {
    val measurements by viewModel.savedMeasurements.collectAsState(initial = null)
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    val scope = rememberCoroutineScope()
    val thumbnailSizePx = with(LocalDensity.current) { ThumbnailSize.roundToPx() }
    var previewId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    var isRenaming by rememberSaveable { mutableStateOf(false) }
    var draftName by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var isUpdating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistoryBackground)
            .safeDrawingPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RouletteButton(
                text = "Назад",
                onClick = onBackClick
            )

            Text(
                text = "История измерений",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Graphite
            )
        }

        HorizontalDivider(color = CardBorder)


        val entries = measurements
        if (entries.isNullOrEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (entries == null) {
                    CircularProgressIndicator()
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.History,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = SecondaryText
                        )
                        Text("История пуста", color = Graphite, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { measurement ->
                    val displayName = if (LegacyAutomaticName.matches(measurement.name)) {
                        "Измерение"
                    } else {
                        measurement.name
                    }
                    val thumbnail by produceState<Bitmap?>(
                        null, measurement.id, thumbnailSizePx
                    ) {
                        value = viewModel.loadSavedMeasurementImage(measurement.id, thumbnailSizePx)
                    }
                    MeasurementCard(
                        name = displayName,
                        thumbnail = thumbnail,
                        createdAt = dateFormat.format(Date(measurement.createdAtMillis)),
                        onOpen = { previewId = measurement.id },
                        onRename = {
                            error = null
                            draftName = displayName
                            isRenaming = true
                            selectedId = measurement.id
                        },
                        onDelete = {
                            error = null
                            draftName = displayName
                            isRenaming = false
                            selectedId = measurement.id
                        }
                    )
                }
            }
        }
    }

    previewId?.let { id ->
        MeasurementPreview(
            id = id,
            viewModel = viewModel,
            onClose = { previewId = null }
        )
    }

    val id = selectedId
    if (id != null) {
        AlertDialog(
            onDismissRequest = { if (!isUpdating) selectedId = null },
            containerColor = Color.White,
            title = {
                Text(if (isRenaming) "Переименовать измерение" else "Удалить измерение?")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isRenaming) {
                        OutlinedTextField(
                            value = draftName,
                            onValueChange = { draftName = it },
                            label = { Text("Название") },
                            singleLine = true,
                            enabled = !isUpdating,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(draftName)
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isUpdating && (!isRenaming || draftName.isNotBlank()),
                    onClick = {
                        isUpdating = true
                        error = null
                        scope.launch {
                            try {
                                if (isRenaming) {
                                    viewModel.renameSavedMeasurement(id, draftName)
                                } else {
                                    viewModel.deleteSavedMeasurement(id)
                                }
                                selectedId = null
                            } catch (exception: CancellationException) {
                                throw exception
                            } catch (_: Exception) {
                                error = if (isRenaming) {
                                    "Не удалось переименовать измерение"
                                } else {
                                    "Не удалось удалить измерение"
                                }
                            } finally {
                                isUpdating = false
                            }
                        }
                    }
                ) {
                    if (isRenaming) {
                        Text("Сохранить")
                    } else {
                        Text("Удалить", color = DeleteTint)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedId = null }, enabled = !isUpdating) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun MeasurementCard(
    name: String,
    thumbnail: Bitmap?,
    createdAt: String,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClickLabel = "Открыть снимок", onClick = onOpen)
                .padding(start = 10.dp, top = 10.dp, end = 4.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(ThumbnailSize)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HistoryBackground),
                contentAlignment = Alignment.Center
            ) {
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Outlined.Image, contentDescription = null, tint = SecondaryText)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    color = Graphite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = SecondaryText
                    )
                    Text(text = createdAt, color = SecondaryText, fontSize = 13.sp)
                }
            }
            IconButton(onClick = onRename) {
                Icon(Icons.Outlined.Edit, contentDescription = "Переименовать", tint = SecondaryText)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Удалить", tint = DeleteTint)
            }
        }
    }
}

@Composable
private fun MeasurementPreview(
    id: Long,
    viewModel: MeasurementViewModel,
    onClose: () -> Unit
) {
    var image by remember(id) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(id) { mutableStateOf(true) }
    LaunchedEffect(id) {
        image = viewModel.loadSavedMeasurementImage(id)
        isLoading = false
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black).safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = image
            when {
                isLoading -> CircularProgressIndicator(color = Color.White)
                bitmap != null -> Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Снимок измерения",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                else -> Text("Снимок недоступен", color = Color.White)
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(24.dp))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Закрыть снимок", tint = Color.White)
            }
        }
    }
}
