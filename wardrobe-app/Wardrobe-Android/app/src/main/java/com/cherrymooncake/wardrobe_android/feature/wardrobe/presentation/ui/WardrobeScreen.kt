package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cherrymooncake.wardrobe_android.core.utils.getFileFromUri
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.WardrobeEvent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.WardrobeIntent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components.AddItemBottomSheet
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components.CategoryPill
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components.EditItemBottomSheet
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components.ItemCard
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components.ItemDetailDialog
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.viewmodel.WardrobeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    onNavigateToMaskEditor: (String) -> Unit,
    viewModel: WardrobeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var itemToDeleteId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.processIntent(WardrobeIntent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is WardrobeEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Мой гардероб") },
                actions = {
                    IconButton(onClick = { viewModel.processIntent(WardrobeIntent.SetAddModalVisible(true)) }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить вещь")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.processIntent(WardrobeIntent.UpdateSearchQuery(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                placeholder = { Text("Поиск по названию...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryPill(
                        text = "Все вещи",
                        isSelected = state.selectedCategoryId == null,
                        onClick = { viewModel.processIntent(WardrobeIntent.SelectCategory(null)) }
                    )
                }
                items(state.categories) { category ->
                    CategoryPill(
                        text = category.name,
                        isSelected = state.selectedCategoryId == category.id,
                        onClick = { viewModel.processIntent(WardrobeIntent.SelectCategory(category.id)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading && state.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ничего не найдено", style = MaterialTheme.typography.bodyLarge)
                }
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.items) { item ->
                        ItemCard(
                            item = item,
                            onDeleteClick = { itemToDeleteId = item.id },
                            onMaskClick = { onNavigateToMaskEditor(item.id) },
                            modifier = Modifier.clickable {
                                viewModel.processIntent(WardrobeIntent.ShowItemDetail(item))
                            }
                        )
                    }
                }
            }
        }
    }


    if (state.selectedItemForDetail != null) {
        ItemDetailDialog(
            item = state.selectedItemForDetail!!,
            onDismiss = { viewModel.processIntent(WardrobeIntent.ShowItemDetail(null)) },
            onDeleteClick = {
                itemToDeleteId = state.selectedItemForDetail!!.id
                viewModel.processIntent(WardrobeIntent.ShowItemDetail(null))
            },
            onMaskClick = {
                onNavigateToMaskEditor(state.selectedItemForDetail!!.id)
                viewModel.processIntent(WardrobeIntent.ShowItemDetail(null))
            },
            onEditClick = {
                viewModel.processIntent(WardrobeIntent.SetEditModalVisible(true))
            }
        )
    }


    if (itemToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { itemToDeleteId = null },
            title = { Text("Удаление") },
            text = { Text("Вы уверены, что хотите удалить эту вещь?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDeleteId?.let { id ->
                            viewModel.processIntent(WardrobeIntent.DeleteItem(id))
                        }
                        itemToDeleteId = null
                    }
                ) {
                    Text("Да, удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDeleteId = null }) {
                    Text("Отмена")
                }
            }
        )
    }


    if (state.showAddModal) {
        AddItemBottomSheet(
            categories = state.categories,
            tags = state.tags,
            isUploading = state.isAddingItem,
            onDismiss = { viewModel.processIntent(WardrobeIntent.SetAddModalVisible(false)) },
            onSave = { name, desc, uri, cats, selectedTags ->
                val file = getFileFromUri(context, uri)
                if (file != null) {
                    viewModel.processIntent(WardrobeIntent.AddItem(name, desc, file, cats, selectedTags))
                } else {
                    Toast.makeText(context, "Не удалось обработать файл", Toast.LENGTH_SHORT).show()
                }
            },
            onDictionariesUpdated = { viewModel.processIntent(WardrobeIntent.ReloadDictionaries) }
        )
    }


    if (state.showEditModal && state.selectedItemForDetail != null) {
        EditItemBottomSheet(
            item = state.selectedItemForDetail!!,
            categories = state.categories,
            tags = state.tags,
            isSaving = state.isEditingItem,
            onDismiss = { viewModel.processIntent(WardrobeIntent.SetEditModalVisible(false)) },
            onSave = { id, name, desc, cats, tags ->
                viewModel.processIntent(WardrobeIntent.UpdateItem(id, name, desc, cats, tags))
            },
            onDictionariesUpdated = { viewModel.processIntent(WardrobeIntent.ReloadDictionaries) }
        )
    }
}