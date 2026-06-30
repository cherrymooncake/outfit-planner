package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.usecase.SyncCategoriesUseCase
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase.SetDailyOutfitUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitItemPayloadDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.CreateOutfitUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.GetOutfitByIdUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.UpdateOutfitUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitItemUiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.constructor.*
import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateItemPayloadDomainModel
import com.cherrymooncake.wardrobe_android.feature.templates.domain.usecase.CreateTemplateUseCase
import com.cherrymooncake.wardrobe_android.feature.templates.domain.usecase.GetTemplateByIdUseCase
import com.cherrymooncake.wardrobe_android.feature.templates.domain.usecase.UpdateTemplateUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.SyncItemsUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mapper.toUi
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.ItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OutfitConstructorViewModel @Inject constructor(
    private val getOutfitByIdUseCase: GetOutfitByIdUseCase,
    private val createOutfitUseCase: CreateOutfitUseCase,
    private val updateOutfitUseCase: UpdateOutfitUseCase,
    private val syncItemsUseCase: SyncItemsUseCase,
    private val syncCategoriesUseCase: SyncCategoriesUseCase,
    private val getTemplateByIdUseCase: GetTemplateByIdUseCase,
    private val createTemplateUseCase: CreateTemplateUseCase,
    private val updateTemplateUseCase: UpdateTemplateUseCase,

    private val setDailyOutfitUseCase: SetDailyOutfitUseCase
) : BaseViewModel<OutfitConstructorState, OutfitConstructorIntent, OutfitConstructorEvent>(OutfitConstructorState()) {

    private var wardrobeJob: Job? = null

    override fun processIntent(intent: OutfitConstructorIntent) {
        when (intent) {
            is OutfitConstructorIntent.LoadInitialData -> loadData(intent.outfitId, intent.templateId, intent.isTemplateEditMode, intent.targetDate)
            is OutfitConstructorIntent.ReloadDictionaries -> loadDictionaries()

            is OutfitConstructorIntent.AddItemToCanvas -> addItemToCanvas(intent.item)
            is OutfitConstructorIntent.UpdateItemTransform -> updateTransform(intent.id, intent.x, intent.y, intent.scale, intent.rotation)
            is OutfitConstructorIntent.SelectCanvasItem -> selectCanvasItem(intent.id)
            is OutfitConstructorIntent.RemoveCanvasItem -> removeCanvasItem(intent.id)
            is OutfitConstructorIntent.BringForward -> bringForward()
            is OutfitConstructorIntent.SendBackward -> sendBackward()
            is OutfitConstructorIntent.UpdateCanvasSize -> updateState { copy(canvasWidth = intent.width, canvasHeight = intent.height) }
            is OutfitConstructorIntent.SelectWardrobeCategory -> {
                updateState { copy(wardrobeSelectedCategoryId = intent.categoryId) }
                loadWardrobe()
            }

            is OutfitConstructorIntent.SetSaveDialogVisible -> updateState { copy(showSaveDialog = intent.isVisible) }
            is OutfitConstructorIntent.SetCategoryManagerVisible -> updateState { copy(showCategoryManager = intent.isVisible) }
            is OutfitConstructorIntent.UpdateMetadata -> updateState { copy(name = intent.name, description = intent.description) }
            is OutfitConstructorIntent.UpdateOutfitCategories -> updateState { copy(outfitCategoryIds = intent.categoryIds) }
            is OutfitConstructorIntent.SaveOutfit -> saveOutfit()

            is OutfitConstructorIntent.SetSaveTemplateDialogVisible -> updateState { copy(showSaveTemplateDialog = intent.isVisible) }
            is OutfitConstructorIntent.SaveAsTemplate -> saveAsTemplate(intent.name, intent.description)
        }
    }

    private fun loadData(outfitId: String?, templateId: String?, isTemplateEditMode: Boolean, targetDate: String?) {
        updateState { copy(
            isLoading = true,
            outfitId = outfitId,
            sourceTemplateId = templateId,
            isTemplateEditMode = isTemplateEditMode,
            targetDate = targetDate
        ) }

        loadDictionaries()
        loadWardrobe()

        if (outfitId != null) {
            viewModelScope.launch {
                val outfit = getOutfitByIdUseCase(outfitId)
                if (outfit != null) {
                    val uiItems = outfit.items.map {
                        OutfitItemUiModel(
                            id = it.id, itemId = it.itemId, imageUrl = "http://localhost:5202/${it.itemImageUrl.removePrefix("/")}",
                            x = it.x, y = it.y, scale = it.scale, rotation = it.rotation, zIndex = it.zIndex,
                            isSlot = false
                        )
                    }
                    updateState {
                        copy(
                            name = outfit.name,
                            description = outfit.description ?: "",
                            canvasWidth = outfit.canvasWidth,
                            canvasHeight = outfit.canvasHeight,
                            canvasItems = uiItems.sortedBy { it.zIndex },
                            outfitCategoryIds = outfit.categories.map { it.id }.toSet(),
                            isLoading = false
                        )
                    }
                } else {
                    sendEvent(OutfitConstructorEvent.ShowSnackbar("Образ не найден"))
                    sendEvent(OutfitConstructorEvent.NavigateBack)
                }
            }
        } else if (templateId != null) {
            viewModelScope.launch {
                val template = getTemplateByIdUseCase(templateId)
                if (template != null) {
                    val uiItems = template.items.map {
                        OutfitItemUiModel(
                            id = UUID.randomUUID().toString(),
                            itemId = null, imageUrl = null,
                            x = it.x, y = it.y, scale = it.scale, rotation = it.rotation, zIndex = it.zIndex,
                            isSlot = true,
                            categoryIdHint = it.categoryIdHint,
                            categoryName = it.categoryName
                        )
                    }
                    updateState {
                        copy(
                            name = if (isTemplateEditMode) template.name else "",
                            description = if (isTemplateEditMode) template.description ?: "" else "",
                            canvasItems = uiItems.sortedBy { it.zIndex },
                            isLoading = false
                        )
                    }
                } else {
                    sendEvent(OutfitConstructorEvent.ShowSnackbar("Шаблон не найден"))
                    sendEvent(OutfitConstructorEvent.NavigateBack)
                }
            }
        } else {
            updateState { copy(isLoading = false) }
        }
    }

    private fun selectCanvasItem(id: String?) {
        val selectedItem = state.value.canvasItems.find { it.id == id }
        updateState { copy(selectedCanvasItemId = id) }

        if (selectedItem != null && selectedItem.categoryIdHint != null) {
            updateState { copy(wardrobeSelectedCategoryId = selectedItem.categoryIdHint) }
            loadWardrobe()
        } else if (selectedItem != null && selectedItem.isSlot && selectedItem.categoryIdHint == null) {
            updateState { copy(wardrobeSelectedCategoryId = null) }
            loadWardrobe()
        }
    }

    private fun loadDictionaries() {
        syncCategoriesUseCase().onEach { domainList ->
            val wardrobeCats = domainList.filter { it.isItemCategory }.map { it.toUi() }
            val outfitCats = domainList.filter { it.isOutfitCategory }.map { it.toUi() }
            updateState { copy(wardrobeCategories = wardrobeCats, outfitCategoriesDict = outfitCats) }
        }.launchIn(viewModelScope)
    }

    private fun loadWardrobe() {
        wardrobeJob?.cancel()
        wardrobeJob = syncItemsUseCase(
            searchTerm = null,
            categoryId = state.value.wardrobeSelectedCategoryId
        ).onEach { domainItems ->
            updateState { copy(wardrobeItems = domainItems.map { it.toUi() }) }
        }.launchIn(viewModelScope)
    }

    private fun addItemToCanvas(item: ItemUiModel) {
        val st = state.value
        if (st.canvasItems.any { it.itemId == item.id }) {
            sendEvent(OutfitConstructorEvent.ShowSnackbar("Эта вещь уже на холсте"))
            return
        }

        val selectedId = st.selectedCanvasItemId
        val selectedItem = st.canvasItems.find { it.id == selectedId }
        val currentItems = st.canvasItems.toMutableList()

        if (selectedItem != null && selectedItem.isSlot) {
            val index = currentItems.indexOfFirst { it.id == selectedId }
            if (index != -1) {
                currentItems[index] = selectedItem.copy(
                    itemId = item.id,
                    imageUrl = item.imageUrl,
                    isSlot = false
                )
            }
        } else {
            if (selectedItem != null) {
                val index = currentItems.indexOfFirst { it.id == selectedId }
                if (index != -1) {
                    currentItems[index] = selectedItem.copy(
                        itemId = item.id,
                        imageUrl = item.imageUrl
                    )
                }
            } else {
                val nextZIndex = if (currentItems.isEmpty()) 0 else currentItems.maxOf { it.zIndex } + 1
                val newItem = OutfitItemUiModel(
                    id = UUID.randomUUID().toString(),
                    itemId = item.id,
                    imageUrl = item.imageUrl,
                    x = st.canvasWidth / 2f,
                    y = st.canvasHeight / 2f,
                    scale = 1.0f,
                    rotation = 0f,
                    zIndex = nextZIndex,
                    isSlot = false,
                    categoryIdHint = item.categories.firstOrNull()?.id
                )
                currentItems.add(newItem)
                updateState { copy(selectedCanvasItemId = newItem.id) }
            }
        }

        updateState { copy(canvasItems = currentItems) }
    }

    private fun updateTransform(id: String, x: Float, y: Float, scale: Float, rotation: Float) {
        val updatedItems = state.value.canvasItems.map {
            if (it.id == id) it.copy(x = x, y = y, scale = scale, rotation = rotation) else it
        }
        updateState { copy(canvasItems = updatedItems) }
    }

    private fun removeCanvasItem(id: String) {
        val filtered = state.value.canvasItems.filter { it.id != id }
        updateState { copy(canvasItems = filtered, selectedCanvasItemId = null) }
    }

    private fun bringForward() {
        val selectedId = state.value.selectedCanvasItemId ?: return
        val items = state.value.canvasItems.toMutableList()
        val index = items.indexOfFirst { it.id == selectedId }

        if (index in 0 until items.size - 1) {
            val temp = items[index]
            items[index] = items[index + 1]
            items[index + 1] = temp
            items.forEachIndexed { i, item -> items[i] = item.copy(zIndex = i) }
            updateState { copy(canvasItems = items) }
        }
    }

    private fun sendBackward() {
        val selectedId = state.value.selectedCanvasItemId ?: return
        val items = state.value.canvasItems.toMutableList()
        val index = items.indexOfFirst { it.id == selectedId }

        if (index > 0) {
            val temp = items[index]
            items[index] = items[index - 1]
            items[index - 1] = temp
            items.forEachIndexed { i, item -> items[i] = item.copy(zIndex = i) }
            updateState { copy(canvasItems = items) }
        }
    }

    private fun saveOutfit() {
        val st = state.value
        if (st.name.isBlank()) {
            sendEvent(OutfitConstructorEvent.ShowSnackbar("Введите название образа"))
            return
        }

        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            try {
                val payloadItems = st.canvasItems.filter { it.itemId != null }.map {
                    OutfitItemPayloadDomainModel(itemId = it.itemId!!, x = it.x, y = it.y, scale = it.scale, rotation = it.rotation, zIndex = it.zIndex)
                }

                if (payloadItems.isEmpty()) {
                    sendEvent(OutfitConstructorEvent.ShowSnackbar("Добавьте хотя бы одну вещь на холст"))
                    updateState { copy(isSaving = false) }
                    return@launch
                }

                if (st.outfitId == null) {
                    val newOutfit = createOutfitUseCase(st.name, st.description.takeIf { it.isNotBlank() }, st.canvasWidth, st.canvasHeight, st.sourceTemplateId, payloadItems, st.outfitCategoryIds.toList())

                    if (st.targetDate != null) {
                        setDailyOutfitUseCase(st.targetDate, newOutfit.id)
                        sendEvent(OutfitConstructorEvent.ShowSnackbar("Образ создан и привязан ко дню!"))
                    } else {
                        sendEvent(OutfitConstructorEvent.ShowSnackbar("Образ создан!"))
                    }
                } else {
                    updateOutfitUseCase(st.outfitId, st.name, st.description.takeIf { it.isNotBlank() }, st.canvasWidth, st.canvasHeight, st.sourceTemplateId, payloadItems, st.outfitCategoryIds.toList())
                    sendEvent(OutfitConstructorEvent.ShowSnackbar("Образ обновлен!"))
                }

                updateState { copy(showSaveDialog = false) }
                sendEvent(OutfitConstructorEvent.NavigateBack)
            } catch (e: Exception) {
                sendEvent(OutfitConstructorEvent.ShowSnackbar("Ошибка: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isSaving = false) }
            }
        }
    }

    private fun saveAsTemplate(name: String, description: String) {
        val st = state.value
        viewModelScope.launch {
            updateState { copy(isSavingTemplate = true) }
            try {
                val templateItems = st.canvasItems.map { uiModel ->
                    TemplateItemPayloadDomainModel(
                        categoryIdHint = uiModel.categoryIdHint,
                        x = uiModel.x,
                        y = uiModel.y,
                        scale = uiModel.scale,
                        rotation = uiModel.rotation,
                        zIndex = uiModel.zIndex
                    )
                }

                if (st.isTemplateEditMode && st.sourceTemplateId != null) {
                    updateTemplateUseCase(st.sourceTemplateId, name, description.takeIf { it.isNotBlank() }, templateItems)
                    sendEvent(OutfitConstructorEvent.ShowSnackbar("Шаблон обновлен!"))
                } else {
                    createTemplateUseCase(name, description.takeIf { it.isNotBlank() }, templateItems)
                    sendEvent(OutfitConstructorEvent.ShowSnackbar("Шаблон создан!"))
                }

                updateState { copy(showSaveTemplateDialog = false) }
                if (st.isTemplateEditMode) {
                    sendEvent(OutfitConstructorEvent.NavigateBack)
                }
            } catch (e: Exception) {
                sendEvent(OutfitConstructorEvent.ShowSnackbar("Ошибка: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isSavingTemplate = false) }
            }
        }
    }
}