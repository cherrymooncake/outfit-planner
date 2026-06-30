package com.cherrymooncake.wardrobe_android.feature.common.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cherrymooncake.wardrobe_android.feature.common.presentation.mvi.DictionaryIntent
import com.cherrymooncake.wardrobe_android.feature.common.presentation.viewmodel.DictionaryViewModel

@Composable
fun TagManagerDialog(
    onDismiss: () -> Unit,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var newTagName by remember { mutableStateOf("") }

    var editingId by remember { mutableStateOf<String?>(null) }
    var editingName by remember { mutableStateOf("") }

    val filteredTags = state.tags.filter { it.isItemTag }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Управление тегами") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Новый тег...") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newTagName.isNotBlank()) {
                                viewModel.processIntent(DictionaryIntent.AddTag(newTagName, isOutfit = false, isItem = true))
                                newTagName = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Icon(Icons.Default.Add, "Добавить", tint = Color.White) }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                if (state.isLoading && filteredTags.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else if (filteredTags.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Нет тегов", color = Color.Gray) }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredTags) { tag ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (editingId == tag.id) {
                                    OutlinedTextField(
                                        value = editingName, onValueChange = { editingName = it },
                                        modifier = Modifier.weight(1f).height(50.dp), singleLine = true
                                    )
                                    Row {
                                        IconButton(onClick = {
                                            viewModel.processIntent(DictionaryIntent.UpdateTag(tag.id, editingName))
                                            editingId = null
                                        }) { Icon(Icons.Default.Check, "Сохранить", tint = Color(0xFF67C23A)) }
                                        IconButton(onClick = { editingId = null }) { Icon(Icons.Default.Close, "Отмена", tint = Color.Gray) }
                                    }
                                } else {
                                    Text(tag.name, modifier = Modifier.weight(1f))
                                    Row {
                                        IconButton(onClick = { editingId = tag.id; editingName = tag.name }) {
                                            Icon(Icons.Default.Edit, "Изменить", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { viewModel.processIntent(DictionaryIntent.DeleteTag(tag.id)) }) {
                                            Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                            Divider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Закрыть") } }
    )
}