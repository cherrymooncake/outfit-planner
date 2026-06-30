package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.OutfitsEvent
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.OutfitsIntent
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components.EditOutfitMetaBottomSheet
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components.OutfitCard
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components.OutfitDetailDialog
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.viewmodel.OutfitsViewModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components.CategoryPill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(
    onNavigateToConstructor: (outfitId: String?, templateId: String?, isTemplateEdit: Boolean) -> Unit,
    viewModel: OutfitsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var outfitToDeleteId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.processIntent(OutfitsIntent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is OutfitsEvent.ShowSnackbar -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Мои образы") },
                actions = {
                    IconButton(onClick = { viewModel.processIntent(OutfitsIntent.SetTemplateDialogVisible(true)) }) {
                        Icon(Icons.Default.Add, "Создать образ")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.processIntent(OutfitsIntent.UpdateSearchQuery(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                placeholder = { Text("Поиск по названию...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryPill(
                        text = "Все образы",
                        isSelected = state.selectedCategoryId == null,
                        onClick = { viewModel.processIntent(OutfitsIntent.SelectCategory(null)) }
                    )
                }
                items(state.categories) { category ->
                    CategoryPill(
                        text = category.name,
                        isSelected = state.selectedCategoryId == category.id,
                        onClick = { viewModel.processIntent(OutfitsIntent.SelectCategory(category.id)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading && state.outfits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (state.outfits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Ничего не найдено") }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.outfits) { outfit ->
                        OutfitCard(
                            outfit = outfit,
                            onClick = { viewModel.processIntent(OutfitsIntent.ShowOutfitDetail(outfit)) },
                            onEditClick = { onNavigateToConstructor(outfit.id, null, false) },
                            onDeleteClick = { outfitToDeleteId = outfit.id }
                        )
                    }
                }
            }
        }
    }

    if (outfitToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { outfitToDeleteId = null },
            title = { Text("Удаление") },
            text = { Text("Точно удалить образ?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.processIntent(OutfitsIntent.DeleteOutfit(outfitToDeleteId!!))
                    outfitToDeleteId = null
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { outfitToDeleteId = null }) { Text("Отмена") } }
        )
    }

    if (state.selectedOutfitForDetail != null) {
        OutfitDetailDialog(
            outfit = state.selectedOutfitForDetail!!,
            onDismiss = { viewModel.processIntent(OutfitsIntent.ShowOutfitDetail(null)) },
            onDeleteClick = {
                outfitToDeleteId = state.selectedOutfitForDetail!!.id
                viewModel.processIntent(OutfitsIntent.ShowOutfitDetail(null))
            },
            onEditClick = {
                onNavigateToConstructor(state.selectedOutfitForDetail!!.id, null, false)
                viewModel.processIntent(OutfitsIntent.ShowOutfitDetail(null))
            },
            onEditMetaClick = {
                viewModel.processIntent(OutfitsIntent.SetEditMetaModalVisible(true))
            }
        )
    }

    if (state.showEditMetaModal && state.selectedOutfitForDetail != null) {
        EditOutfitMetaBottomSheet(
            outfit = state.selectedOutfitForDetail!!,
            categories = state.categories,
            isSaving = state.isEditingMeta,
            onDismiss = { viewModel.processIntent(OutfitsIntent.SetEditMetaModalVisible(false)) },
            onSave = { name, desc, cats ->
                viewModel.processIntent(OutfitsIntent.UpdateOutfitMeta(state.selectedOutfitForDetail!!.id, name, desc, cats))
            },
            onDictionariesUpdated = { viewModel.processIntent(OutfitsIntent.LoadInitialData) }
        )
    }

    if (state.showTemplateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.processIntent(OutfitsIntent.SetTemplateDialogVisible(false)) },
            title = { Text("Выберите шаблон", style = MaterialTheme.typography.titleLarge) },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            onClick = {
                                viewModel.processIntent(OutfitsIntent.SetTemplateDialogVisible(false))
                                onNavigateToConstructor(null, null, false)
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Пустой холст", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            }
                        }
                    }

                    items(state.templates) { tpl ->
                        Card(
                            onClick = {
                                viewModel.processIntent(OutfitsIntent.SetTemplateDialogVisible(false))
                                onNavigateToConstructor(null, tpl.id, false)
                            }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = tpl.name,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${tpl.items.size} слотов",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            viewModel.processIntent(OutfitsIntent.SetTemplateDialogVisible(false))
                                            onNavigateToConstructor(null, tpl.id, true)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { viewModel.processIntent(OutfitsIntent.DeleteTemplate(tpl.id)) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.processIntent(OutfitsIntent.SetTemplateDialogVisible(false)) }) {
                    Text("Отмена")
                }
            }
        )
    }
}