package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cherrymooncake.wardrobe_android.feature.common.presentation.ui.CategoryManagerDialog
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditOutfitMetaBottomSheet(
    outfit: OutfitUiModel,
    categories: List<CategoryUiModel>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, desc: String?, cats: List<String>) -> Unit,
    onDictionariesUpdated: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(outfit.name) }
    var description by remember { mutableStateOf(outfit.description ?: "") }
    var selectedCategoryIds by remember { mutableStateOf(outfit.categories.map { it.id }.toSet()) }
    var showCategoryManager by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Свойства образа", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Категории", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = { showCategoryManager = true }) { Icon(Icons.Default.Settings, "Управление") }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    val isSelected = selectedCategoryIds.contains(category.id)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryIds = if (isSelected) selectedCategoryIds - category.id else selectedCategoryIds + category.id },
                        label = { Text(category.name) }
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSaving && name.isNotBlank(),
                onClick = { onSave(name, description.takeIf { it.isNotBlank() }, selectedCategoryIds.toList()) }
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("Сохранить изменения")
            }
        }
    }

    if (showCategoryManager) {
        CategoryManagerDialog(mode = "outfit", onDismiss = { showCategoryManager = false; onDictionariesUpdated() })
    }
}