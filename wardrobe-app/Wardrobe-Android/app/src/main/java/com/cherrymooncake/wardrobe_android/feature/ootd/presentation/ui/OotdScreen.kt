package com.cherrymooncake.wardrobe_android.feature.ootd.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.OotdEvent
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.OotdIntent
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.viewmodel.OotdViewModel
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components.OutfitPreview
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OotdScreen(
    targetDate: String?,
    onNavigateToConstructor: (String) -> Unit,
    viewModel: OotdViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isToday = remember(state.targetDate) {
        try { LocalDate.parse(state.targetDate) == LocalDate.now() } catch (e: Exception) { false }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.processIntent(OotdIntent.LoadData(targetDate ?: ""))
                if (targetDate == null || targetDate == LocalDate.now().toString()) {
                    viewModel.processIntent(OotdIntent.LoadSavedCity)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            if (event is OotdEvent.ShowSnackbar) {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (state.showCityDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.processIntent(OotdIntent.SetCityDialogVisible(false)) },
            title = { Text("Укажите город") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.citySearchQuery,
                        onValueChange = { viewModel.processIntent(OotdIntent.UpdateCitySearchQuery(it)) },
                        label = { Text("Название (например: Минск)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (state.isCitySearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFFD128A1),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )

                    if (state.citySearchResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        ) {
                            items(state.citySearchResults.size) { index ->
                                val city = state.citySearchResults[index]
                                val subtitle = listOfNotNull(city.region, city.country).joinToString(", ")

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.processIntent(OotdIntent.SelectCitySuggestion(city.name))
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(text = city.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    if (subtitle.isNotEmpty()) {
                                        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                                if (index < state.citySearchResults.size - 1) {
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (state.citySearchQuery.isNotBlank()) {
                        viewModel.processIntent(OotdIntent.SaveCity(state.citySearchQuery))
                    }
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.processIntent(OotdIntent.SetCityDialogVisible(false)) }) {
                    Text("Отмена")
                }
            }
        )
    }

    val dateToDisplay = remember(state.targetDate) {
        try {
            val date = LocalDate.parse(state.targetDate)
            val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
            val prefix = if (date == LocalDate.now()) "Сегодня, " else ""
            prefix + date.format(formatter)
        } catch (e: Exception) {
            state.targetDate
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(
            title = { Text("Образ дня") },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateToDisplay,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            if (isToday) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f).padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF4F9)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                        if (state.isWeatherLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFD128A1))
                        } else if (state.weather != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                        Text(state.weather!!.city, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                                    }
                                    TextButton(onClick = {
                                        viewModel.processIntent(OotdIntent.SetCityDialogVisible(true))
                                    }) { Text("Изменить") }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        imageVector = getWeatherIcon(state.weather!!.condition),
                                        contentDescription = null,
                                        tint = getWeatherColor(state.weather!!.condition),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "${state.weather!!.temperature.toInt()}°C",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = state.weather!!.condition,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.DarkGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "💡 ${state.weather!!.recommendation}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFD128A1),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (state.weatherError != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(state.weatherError!!, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.width(8.dp))
                                TextButton(onClick = {
                                    viewModel.processIntent(OotdIntent.SetCityDialogVisible(true))
                                }) { Text("Указать город") }
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.processIntent(OotdIntent.SetCityDialogVisible(true))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD128A1))
                            ) {
                                Icon(Icons.Default.LocationOn, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Указать город для прогноза")
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.outfit != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f).weight(1f).padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFF5F7FA)).padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            OutfitPreview(outfit = state.outfit!!, modifier = Modifier.fillMaxSize())
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.outfit!!.name, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Button(onClick = { viewModel.processIntent(OotdIntent.SetSelectDialogVisible(true)) }) {
                                    Text("Изменить")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.processIntent(OotdIntent.ClearOutfit) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) { Text("Сбросить") }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("На этот день образ не запланирован", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.processIntent(OotdIntent.SetAiDialogVisible(true)) },
                            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF67C23A))
                        ) {
                            Icon(Icons.Default.AutoAwesome, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Не знаете что надеть?")
                        }

                        Button(
                            onClick = { viewModel.processIntent(OotdIntent.LoadAllOutfits) },
                            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                        ) {
                            Icon(Icons.Default.Checkroom, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Выбрать из шкафа")
                        }

                        Button(
                            onClick = { viewModel.processIntent(OotdIntent.SetRandomOutfit) },
                            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6A23C))
                        ) {
                            Icon(Icons.Default.AutoAwesome, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Случайный образ")
                        }

                        OutlinedButton(
                            onClick = { onNavigateToConstructor(state.targetDate) },
                            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                        ) {
                            Icon(Icons.Default.Brush, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Создать новый")
                        }
                    }
                }
            }
        }
    }

    if (state.showAiDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.processIntent(OotdIntent.SetAiDialogVisible(false)) },
            title = { Text("ИИ-Стилист", fontWeight = FontWeight.Bold, color = Color(0xFFD128A1)) },
            text = {
                if (state.aiRecommendedOutfit == null) {
                    Column {
                        Text("Опишите, куда вы собираетесь, и я подберу образ с учетом погоды!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.aiPrompt,
                            onValueChange = { viewModel.processIntent(OotdIntent.UpdateAiPrompt(it)) },
                            placeholder = { Text("Например: Хочу что-то удобное...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✨ ${state.aiExplanation}",
                            color = Color(0xFFD128A1),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF5F7FA)).padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            OutfitPreview(outfit = state.aiRecommendedOutfit!!, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            },
            confirmButton = {
                if (state.aiRecommendedOutfit == null) {
                    Button(
                        onClick = { viewModel.processIntent(OotdIntent.AskAiStylist) },
                        enabled = !state.isAiLoading && state.aiPrompt.isNotBlank()
                    ) {
                        if (state.isAiLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        else Text("Подобрать")
                    }
                } else {
                    Button(onClick = { viewModel.processIntent(OotdIntent.ApplyAiRecommendation) }) {
                        Text("Надеть это")
                    }
                }
            },
            dismissButton = {
                if (state.aiRecommendedOutfit != null) {
                    TextButton(onClick = { viewModel.processIntent(OotdIntent.SetAiDialogVisible(false)) }) { Text("Закрыть") }
                } else {
                    TextButton(onClick = { viewModel.processIntent(OotdIntent.SetAiDialogVisible(false)) }) { Text("Отмена") }
                }
            }
        )
    }

    if (state.showSelectDialog) {
        Dialog(
            onDismissRequest = { viewModel.processIntent(OotdIntent.SetSelectDialogVisible(false)) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Выберите образ", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    if (state.isDialogLoading) {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    } else if (state.allOutfits.isEmpty()) {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Нет сохраненных образов", color = Color.Gray) }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.allOutfits) { outfit ->
                                Card(
                                    modifier = Modifier.clickable { viewModel.processIntent(OotdIntent.SelectOutfit(outfit.id)) },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(8.dp)).background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            OutfitPreview(outfit = outfit, modifier = Modifier.fillMaxSize())
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(outfit.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    TextButton(
                        onClick = { viewModel.processIntent(OotdIntent.SetSelectDialogVisible(false)) },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("Отмена") }
                }
            }
        }
    }
}

fun getWeatherIcon(condition: String): ImageVector {
    return when (condition) {
        "Ясно" -> Icons.Default.WbSunny
        "Снег" -> Icons.Default.AcUnit
        "Дождь", "Гроза" -> Icons.Default.WaterDrop
        else -> Icons.Default.Cloud
    }
}

fun getWeatherColor(condition: String): Color {
    return when (condition) {
        "Ясно" -> Color(0xFFE6A23C)
        "Дождь", "Гроза" -> Color(0xFF409EFF)
        "Снег" -> Color(0xFFA0CFFF)
        else -> Color.Gray
    }
}