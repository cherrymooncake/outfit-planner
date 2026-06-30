package com.cherrymooncake.wardrobe_android.feature.common.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
fun CategoryManagerDialog(
    mode: String = "item",
    onDismiss: () -> Unit,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var newCategoryName by remember { mutableStateOf("") }

    var editingId by remember { mutableStateOf<String?>(null) }
    var editingName by remember { mutableStateOf("") }

    val filteredCategories = state.categories.filter {
        if (mode == "item") it.isItemCategory else it.isOutfitCategory
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == "item") "Категории вещей" else "Категории образов") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Новая категория...") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                viewModel.processIntent(
                                    DictionaryIntent.AddCategory(
                                        name = newCategoryName,
                                        isItem = mode == "item",
                                        isOutfit = mode == "outfit"
                                    )
                                )
                                newCategoryName = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, "Добавить", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                if (state.isLoading && filteredCategories.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (filteredCategories.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Нет категорий", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredCategories) { cat ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (editingId == cat.id) {
                                    OutlinedTextField(
                                        value = editingName,
                                        onValueChange = { editingName = it },
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        singleLine = true
                                    )
                                    Row {
                                        IconButton(onClick = {
                                            viewModel.processIntent(DictionaryIntent.UpdateCategory(cat.id, editingName))
                                            editingId = null
                                        }) { Icon(Icons.Default.Check, "Сохранить", tint = Color(0xFF67C23A)) }
                                        IconButton(onClick = { editingId = null }) {
                                            Icon(Icons.Default.Close, "Отмена", tint = Color.Gray)
                                        }
                                    }
                                } else {
                                    Text(cat.name, modifier = Modifier.weight(1f))
                                    Row {
                                        IconButton(onClick = {
                                            editingId = cat.id
                                            editingName = cat.name
                                        }) { Icon(Icons.Default.Edit, "Изменить", tint = MaterialTheme.colorScheme.primary) }

                                        IconButton(onClick = {
                                            viewModel.processIntent(DictionaryIntent.DeleteCategory(cat.id))
                                        }) { Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error) }
                                    }
                                }
                            }
                            Divider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}