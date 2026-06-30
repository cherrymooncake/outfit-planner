package com.cherrymooncake.wardrobe_android.feature.ootd.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.CalendarEvent
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.CalendarIntent
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.viewmodel.CalendarViewModel
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components.OutfitPreview
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onDayClick: (String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            if (event is CalendarEvent.ShowSnackbar) Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
    val monthTitle = state.currentMonth.format(formatter).replaceFirstChar { it.uppercase() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Календарь образов") },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.processIntent(CalendarIntent.PrevMonth) }) {
                    Icon(Icons.Default.ChevronLeft, "Предыдущий месяц")
                }
                Text(monthTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = { viewModel.processIntent(CalendarIntent.NextMonth) }) {
                    Icon(Icons.Default.ChevronRight, "Следующий месяц")
                }
            }

            if (state.isLoading) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Card(
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    val daysInMonth = state.currentMonth.lengthOfMonth()
                    val firstDayOffset = state.currentMonth.atDay(1).dayOfWeek.value - 1

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        items(7) { index ->
                            Text(
                                text = daysOfWeek[index],
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(firstDayOffset) { Spacer(Modifier.aspectRatio(0.8f)) }

                        items(daysInMonth) { dayIndex ->
                            val day = dayIndex + 1
                            val dateObj = state.currentMonth.atDay(day)
                            val dateStr = dateObj.toString()
                            val outfit = state.monthData[dateStr]
                            val isToday = dateObj == LocalDate.now()

                            Box(
                                modifier = Modifier
                                    .aspectRatio(0.8f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isToday) Color(0xFFFCEBF0) else Color.Transparent)
                                    .border(
                                        width = if (isToday) 2.dp else 1.dp,
                                        color = if (isToday) Color(0xFFD128A1) else Color(0xFFE4E7ED),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onDayClick(dateStr) },
                                contentAlignment = Alignment.TopStart
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) Color(0xFFD128A1) else Color.Black,
                                    modifier = Modifier.padding(4.dp)
                                )

                                if (outfit != null) {
                                    Box(
                                        modifier = Modifier.fillMaxSize().padding(top = 16.dp, bottom = 4.dp, start = 4.dp, end = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        OutfitPreview(outfit = outfit, modifier = Modifier.fillMaxSize())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}