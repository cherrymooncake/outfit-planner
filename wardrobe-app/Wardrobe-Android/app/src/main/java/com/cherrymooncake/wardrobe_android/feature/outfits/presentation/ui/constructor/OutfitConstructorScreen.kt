package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.constructor

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.cherrymooncake.wardrobe_android.feature.common.presentation.ui.CategoryManagerDialog
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.constructor.OutfitConstructorEvent
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.constructor.OutfitConstructorIntent
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.viewmodel.OutfitConstructorViewModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.components.CategoryPill
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.text.style.TextOverflow

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OutfitConstructorScreen(
    outfitId: String?,
    templateId: String?,
    isTemplateEditMode: Boolean,
    targetDate: String?,
    onNavigateBack: () -> Unit,
    viewModel: OutfitConstructorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showSizeDialog by remember { mutableStateOf(false) }
    var tempWidth by remember { mutableIntStateOf(state.canvasWidth) }
    var tempHeight by remember { mutableIntStateOf(state.canvasHeight) }

    var tempTemplateName by remember { mutableStateOf("") }
    var tempTemplateDesc by remember { mutableStateOf("") }

    LaunchedEffect(outfitId, templateId) {
        viewModel.processIntent(OutfitConstructorIntent.LoadInitialData(outfitId, templateId, isTemplateEditMode, targetDate))
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is OutfitConstructorEvent.ShowSnackbar -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is OutfitConstructorEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isTemplateEditMode) "Правка шаблона" else if (outfitId == null) "Новый образ" else "Редактирование",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Назад") } },
                    actions = {
                        IconButton(onClick = {
                            tempWidth = state.canvasWidth
                            tempHeight = state.canvasHeight
                            showSizeDialog = true
                        }) {
                            Icon(Icons.Default.AspectRatio, "Размер холста", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        TextButton(onClick = {
                            tempTemplateName = if (isTemplateEditMode) state.name else ""
                            tempTemplateDesc = if (isTemplateEditMode) state.description else ""
                            viewModel.processIntent(OutfitConstructorIntent.SetSaveTemplateDialogVisible(true))
                        }) {
                            Text(
                                text = if (isTemplateEditMode) "Обновить" else "В шаблон",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (!isTemplateEditMode) {
                            IconButton(onClick = { viewModel.processIntent(OutfitConstructorIntent.SetSaveDialogVisible(true)) }) {
                                Icon(Icons.Default.Check, "Сохранить", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
                    .padding(horizontal = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (state.selectedCanvasItemId != null) {
                    val selectedItem = state.canvasItems.find { it.id == state.selectedCanvasItemId }
                    if (selectedItem != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.processIntent(OutfitConstructorIntent.SendBackward) }) {
                                Icon(Icons.Default.FlipToBack, "Назад")
                            }
                            IconButton(onClick = { viewModel.processIntent(OutfitConstructorIntent.BringForward) }) {
                                Icon(Icons.Default.FlipToFront, "Вперед")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(onClick = {
                                viewModel.processIntent(OutfitConstructorIntent.UpdateItemTransform(
                                    selectedItem.id, selectedItem.x, selectedItem.y, selectedItem.scale, selectedItem.rotation - 15f
                                ))
                            }) { Icon(Icons.Default.RotateLeft, "Влево") }

                            IconButton(onClick = {
                                viewModel.processIntent(OutfitConstructorIntent.UpdateItemTransform(
                                    selectedItem.id, selectedItem.x, selectedItem.y, selectedItem.scale, selectedItem.rotation + 15f
                                ))
                            }) { Icon(Icons.Default.RotateRight, "Вправо") }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(onClick = { viewModel.processIntent(OutfitConstructorIntent.RemoveCanvasItem(selectedItem.id)) }) {
                                Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                } else {
                    Text("Выберите вещь на холсте", color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                }
            }

            HorizontalDivider()

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF0F2F5))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { viewModel.processIntent(OutfitConstructorIntent.SelectCanvasItem(null)) })
                    },
                contentAlignment = Alignment.Center
            ) {
                val canvasPxW = constraints.maxWidth.toFloat()
                val canvasPxH = constraints.maxHeight.toFloat()
                val aspectScreen = canvasPxW / canvasPxH
                val aspectLogical = state.canvasWidth.toFloat() / state.canvasHeight.toFloat()

                val renderPxW = if (aspectLogical > aspectScreen) canvasPxW else canvasPxH * aspectLogical
                val renderPxH = if (aspectLogical > aspectScreen) canvasPxW / aspectLogical else canvasPxH

                val scaleFactor = renderPxW / state.canvasWidth.toFloat()
                val density = LocalDensity.current.density

                Box(
                    modifier = Modifier
                        .size((renderPxW / density).dp, (renderPxH / density).dp)
                        .background(Color.White)
                        .clipToBounds()
                ) {
                    val sortedItems = state.canvasItems.sortedBy { it.zIndex }

                    sortedItems.forEach { item ->
                        val isSelected = item.id == state.selectedCanvasItemId

                        var localX by remember(item.id) { mutableFloatStateOf(item.x) }
                        var localY by remember(item.id) { mutableFloatStateOf(item.y) }
                        var localScale by remember(item.id) { mutableFloatStateOf(item.scale) }
                        var localRotation by remember(item.id) { mutableFloatStateOf(item.rotation) }
                        var isDragging by remember { mutableStateOf(false) }

                        LaunchedEffect(item.x, item.y, item.scale, item.rotation) {
                            if (!isDragging) {
                                localX = item.x
                                localY = item.y
                                localScale = item.scale
                                localRotation = item.rotation
                            }
                        }

                        if (item.isSlot) {
                            val slotLogicalSize = 200f * localScale
                            val renderItemWidthPx = slotLogicalSize * scaleFactor
                            val renderItemHeightPx = slotLogicalSize * scaleFactor

                            val offsetX = (localX * scaleFactor) - (renderItemWidthPx / 2)
                            val offsetY = (localY * scaleFactor) - (renderItemHeightPx / 2)

                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                                    .size((renderItemWidthPx / density).dp, (renderItemHeightPx / density).dp)
                                    .graphicsLayer {
                                        rotationZ = localRotation
                                        transformOrigin = TransformOrigin.Center
                                    }
                                    .pointerInput(item.id) {
                                        awaitEachGesture {
                                            val down = awaitFirstDown(requireUnconsumed = false)
                                            down.consume()
                                            isDragging = true

                                            if (state.selectedCanvasItemId != item.id) {
                                                viewModel.processIntent(OutfitConstructorIntent.SelectCanvasItem(item.id))
                                            }

                                            do {
                                                val event = awaitPointerEvent()
                                                val zoomChange = event.calculateZoom()
                                                val panChange = event.calculatePan()
                                                val rotationChange = event.calculateRotation()

                                                if (zoomChange != 1f || panChange != Offset.Zero || rotationChange != 0f) {
                                                    val angleRad = Math.toRadians(localRotation.toDouble())
                                                    val cosValue = cos(angleRad).toFloat()
                                                    val sinValue = sin(angleRad).toFloat()

                                                    val screenPanX = panChange.x * cosValue - panChange.y * sinValue
                                                    val screenPanY = panChange.x * sinValue + panChange.y * cosValue

                                                    val logicalDx = screenPanX / scaleFactor
                                                    val logicalDy = screenPanY / scaleFactor

                                                    localX += logicalDx
                                                    localY += logicalDy
                                                    localScale *= zoomChange
                                                    localRotation += rotationChange

                                                    viewModel.processIntent(
                                                        OutfitConstructorIntent.UpdateItemTransform(
                                                            id = item.id,
                                                            x = localX,
                                                            y = localY,
                                                            scale = localScale,
                                                            rotation = localRotation
                                                        )
                                                    )
                                                }

                                                event.changes.forEach {
                                                    if (it.positionChanged()) it.consume()
                                                }
                                            } while (event.changes.any { it.pressed })

                                            isDragging = false
                                        }
                                    }
                                    .background(Color(0xFFE6A23C).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFD128A1) else Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.categoryName ?: "Любая вещь",
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                        } else {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.imageUrl)
                                    .size(coil.size.Size.ORIGINAL)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null
                            ) {
                                val currentPainter = painter
                                val pState = currentPainter.state
                                var intrinsicSize by remember { mutableStateOf(Size.Zero) }

                                if (pState is AsyncImagePainter.State.Success) {
                                    val size = pState.painter.intrinsicSize
                                    if (size.width > 0 && size.height > 0) {
                                        intrinsicSize = size
                                    }
                                }

                                if (intrinsicSize != Size.Zero) {
                                    val logicalItemWidth = intrinsicSize.width * localScale
                                    val logicalItemHeight = intrinsicSize.height * localScale

                                    val renderItemWidthPx = logicalItemWidth * scaleFactor
                                    val renderItemHeightPx = logicalItemHeight * scaleFactor

                                    val offsetX = (localX * scaleFactor) - (renderItemWidthPx / 2)
                                    val offsetY = (localY * scaleFactor) - (renderItemHeightPx / 2)

                                    Box(
                                        modifier = Modifier
                                            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                                            .size(
                                                width = (renderItemWidthPx / density).dp,
                                                height = (renderItemHeightPx / density).dp
                                            )
                                            .graphicsLayer {
                                                rotationZ = localRotation
                                                transformOrigin = TransformOrigin.Center
                                            }
                                            .pointerInput(item.id) {
                                                awaitEachGesture {
                                                    val down = awaitFirstDown(requireUnconsumed = false)
                                                    down.consume()
                                                    isDragging = true

                                                    if (state.selectedCanvasItemId != item.id) {
                                                        viewModel.processIntent(OutfitConstructorIntent.SelectCanvasItem(item.id))
                                                    }

                                                    do {
                                                        val event = awaitPointerEvent()
                                                        val zoomChange = event.calculateZoom()
                                                        val panChange = event.calculatePan()
                                                        val rotationChange = event.calculateRotation()

                                                        if (zoomChange != 1f || panChange != Offset.Zero || rotationChange != 0f) {
                                                            val angleRad = Math.toRadians(localRotation.toDouble())
                                                            val cosValue = cos(angleRad).toFloat()
                                                            val sinValue = sin(angleRad).toFloat()

                                                            val screenPanX = panChange.x * cosValue - panChange.y * sinValue
                                                            val screenPanY = panChange.x * sinValue + panChange.y * cosValue

                                                            val logicalDx = screenPanX / scaleFactor
                                                            val logicalDy = screenPanY / scaleFactor

                                                            localX += logicalDx
                                                            localY += logicalDy
                                                            localScale *= zoomChange
                                                            localRotation += rotationChange

                                                            viewModel.processIntent(
                                                                OutfitConstructorIntent.UpdateItemTransform(
                                                                    id = item.id,
                                                                    x = localX,
                                                                    y = localY,
                                                                    scale = localScale,
                                                                    rotation = localRotation
                                                                )
                                                            )
                                                        }

                                                        event.changes.forEach {
                                                            if (it.positionChanged()) it.consume()
                                                        }
                                                    } while (event.changes.any { it.pressed })

                                                    isDragging = false
                                                }
                                            }
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) Color(0xFFD128A1) else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = currentPainter,
                                            contentDescription = null,
                                            contentScale = ContentScale.FillBounds,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.offset {
                                            IntOffset(
                                                x = (localX * scaleFactor - 50).toInt(),
                                                y = (localY * scaleFactor - 50).toInt()
                                            )
                                        },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(vertical = 12.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryPill(
                            text = "Все",
                            isSelected = state.wardrobeSelectedCategoryId == null,
                            onClick = { viewModel.processIntent(OutfitConstructorIntent.SelectWardrobeCategory(null)) }
                        )
                    }
                    items(state.wardrobeCategories) { cat ->
                        CategoryPill(
                            text = cat.name,
                            isSelected = state.wardrobeSelectedCategoryId == cat.id,
                            onClick = { viewModel.processIntent(OutfitConstructorIntent.SelectWardrobeCategory(cat.id)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.wardrobeItems) { item ->
                        val isAlreadyUsed = state.canvasItems.any { it.itemId == item.id }

                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable(enabled = !isAlreadyUsed) {
                                    viewModel.processIntent(OutfitConstructorIntent.AddItemToCanvas(item))
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = if (isAlreadyUsed) 0.4f else 1f }
                                )
                                if (isAlreadyUsed) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .background(Color(0xFF67C23A), RoundedCornerShape(50))
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp).padding(4.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(topStart = 8.dp))
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSizeDialog) {
        AlertDialog(
            onDismissRequest = { showSizeDialog = false },
            title = { Text("Настройки холста") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempWidth.toString(),
                        onValueChange = { tempWidth = it.toIntOrNull() ?: 0 },
                        label = { Text("Ширина") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempHeight.toString(),
                        onValueChange = { tempHeight = it.toIntOrNull() ?: 0 },
                        label = { Text("Высота") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.processIntent(OutfitConstructorIntent.UpdateCanvasSize(tempWidth, tempHeight))
                    showSizeDialog = false
                }) { Text("Применить") }
            },
            dismissButton = {
                TextButton(onClick = { showSizeDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (state.showSaveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.processIntent(OutfitConstructorIntent.SetSaveDialogVisible(false)) },
            title = { Text("Сохранить образ") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.processIntent(OutfitConstructorIntent.UpdateMetadata(it, state.description)) },
                        label = { Text("Название") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.processIntent(OutfitConstructorIntent.UpdateMetadata(state.name, it)) },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Категории", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.processIntent(OutfitConstructorIntent.SetCategoryManagerVisible(true)) }) {
                            Icon(Icons.Default.Settings, "Управление")
                        }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.outfitCategoriesDict.forEach { category ->
                            val isSelected = state.outfitCategoryIds.contains(category.id)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newSet = if (isSelected) state.outfitCategoryIds - category.id else state.outfitCategoryIds + category.id
                                    viewModel.processIntent(OutfitConstructorIntent.UpdateOutfitCategories(newSet))
                                },
                                label = { Text(category.name) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.processIntent(OutfitConstructorIntent.SaveOutfit) },
                    enabled = !state.isSaving && state.name.isNotBlank()
                ) {
                    if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    else Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.processIntent(OutfitConstructorIntent.SetSaveDialogVisible(false)) }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (state.showSaveTemplateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.processIntent(OutfitConstructorIntent.SetSaveTemplateDialogVisible(false)) },
            title = { Text(if (state.isTemplateEditMode) "Обновить шаблон" else "Сохранить как шаблон") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Текущая расстановка вещей на холсте будет сохранена как структура слотов.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = tempTemplateName,
                        onValueChange = { tempTemplateName = it },
                        label = { Text("Название шаблона") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempTemplateDesc,
                        onValueChange = { tempTemplateDesc = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.processIntent(OutfitConstructorIntent.SaveAsTemplate(tempTemplateName, tempTemplateDesc)) },
                    enabled = !state.isSavingTemplate && tempTemplateName.isNotBlank()
                ) {
                    if (state.isSavingTemplate) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    else Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.processIntent(OutfitConstructorIntent.SetSaveTemplateDialogVisible(false)) }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (state.showCategoryManager) {
        CategoryManagerDialog(
            mode = "outfit",
            onDismiss = {
                viewModel.processIntent(OutfitConstructorIntent.SetCategoryManagerVisible(false))
                viewModel.processIntent(OutfitConstructorIntent.ReloadDictionaries)
            }
        )
    }
}