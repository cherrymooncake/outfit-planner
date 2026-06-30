package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cherrymooncake.wardrobe_android.feature.common.presentation.ui.CategoryManagerDialog
import com.cherrymooncake.wardrobe_android.feature.common.presentation.ui.TagManagerDialog
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.TagUiModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddItemBottomSheet(
    categories: List<CategoryUiModel>,
    tags: List<TagUiModel>,
    isUploading: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String?, imageUri: Uri, selectedCategories: List<String>, selectedTags: List<String>) -> Unit,
    onDictionariesUpdated: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategoryIds by remember { mutableStateOf(setOf<String>()) }
    var selectedTagIds by remember { mutableStateOf(setOf<String>()) }

    var showCategoryManager by remember { mutableStateOf(false) }
    var showTagManager by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Добавить новую вещь", style = MaterialTheme.typography.titleLarge)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF0F2F5), MaterialTheme.shapes.medium)
                    .clickable {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        Text("Нажмите, чтобы выбрать фото", color = Color.Gray)
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Категории", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = { showCategoryManager = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Управление категориями")
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    val isSelected = selectedCategoryIds.contains(category.id)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryIds = if (isSelected) selectedCategoryIds - category.id
                            else selectedCategoryIds + category.id
                        },
                        label = { Text(category.name) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Теги", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = { showTagManager = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Управление тегами")
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    val isSelected = selectedTagIds.contains(tag.id)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTagIds = if (isSelected) selectedTagIds - tag.id
                            else selectedTagIds + tag.id
                        },
                        label = { Text(tag.name) }
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isUploading && name.isNotBlank() && selectedImageUri != null,
                onClick = {
                    onSave(name, description.takeIf { it.isNotBlank() }, selectedImageUri!!, selectedCategoryIds.toList(), selectedTagIds.toList())
                }
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Сохранить")
                }
            }

            if (name.isBlank() || selectedImageUri == null) {
                Text(
                    text = "* Для сохранения требуется фото и название",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showCategoryManager) {
        CategoryManagerDialog(
            mode = "item",
            onDismiss = {
                showCategoryManager = false
                onDictionariesUpdated()
            }
        )
    }

    if (showTagManager) {
        TagManagerDialog(
            onDismiss = {
                showTagManager = false
                onDictionariesUpdated()
            }
        )
    }
}