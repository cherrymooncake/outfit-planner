package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OutfitDetailDialog(
    outfit: OutfitUiModel,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditMetaClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = outfit.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp).weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }
                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE5E7EB))
                            .padding(1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutfitPreview(outfit = outfit, modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Описание", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = outfit.description?.takeIf { it.isNotBlank() } ?: "Нет описания",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (outfit.description.isNullOrBlank()) Color.Gray else Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Категории", style = MaterialTheme.typography.titleMedium)
                    if (outfit.categories.isNotEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            outfit.categories.forEach { cat ->
                                AssistChip(onClick = {}, label = { Text(cat.name) })
                            }
                        }
                    } else {
                        Text("Нет категорий", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Количество вещей: ${outfit.items.size}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onEditMetaClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.EditNote, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Свойства")
                        }

                        Button(onClick = onEditClick) {
                            Icon(Icons.Default.Brush, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Конструктор")
                        }
                    }
                }
            }
        }
    }
}