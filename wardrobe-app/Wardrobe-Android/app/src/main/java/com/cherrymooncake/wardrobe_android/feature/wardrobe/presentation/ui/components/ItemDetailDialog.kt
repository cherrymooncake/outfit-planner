package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.ItemUiModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemDetailDialog(
    item: ItemUiModel,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
    onMaskClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
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
                            .height(300.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F2F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Описание", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = item.description?.takeIf { it.isNotBlank() } ?: "Нет описания",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.description.isNullOrBlank()) Color.Gray else Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Категории", style = MaterialTheme.typography.titleMedium)
                    if (item.categories.isNotEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item.categories.forEach { cat ->
                                AssistChip(onClick = {}, label = { Text(cat.name) })
                            }
                        }
                    } else {
                        Text("Нет категорий", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Теги", style = MaterialTheme.typography.titleMedium)
                    if (item.tags.isNotEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item.tags.forEach { tag ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(tag.name) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFF5F5F5))
                                )
                            }
                        }
                    } else {
                        Text("Нет тегов", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, "Изменить", tint = MaterialTheme.colorScheme.primary)
                        }
                        Button(
                            onClick = onMaskClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6A23C))
                        ) {
                            Icon(Icons.Default.ContentCut, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Вырезка")
                        }
                    }
                }
            }
        }
    }
}