package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.usecase.SyncCategoriesUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.DeleteOutfitUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.SyncOutfitsUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.UpdateOutfitUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mapper.toUi
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.OutfitsEvent
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.OutfitsIntent
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.OutfitsState
import com.cherrymooncake.wardrobe_android.feature.templates.domain.usecase.DeleteTemplateUseCase
import com.cherrymooncake.wardrobe_android.feature.templates.domain.usecase.SyncTemplatesUseCase
import com.cherrymooncake.wardrobe_android.feature.templates.presentation.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutfitsViewModel @Inject constructor(
    private val syncOutfitsUseCase: SyncOutfitsUseCase,
    private val syncCategoriesUseCase: SyncCategoriesUseCase,
    private val deleteOutfitUseCase: DeleteOutfitUseCase,
    private val updateOutfitUseCase: UpdateOutfitUseCase,
    private val syncTemplatesUseCase: SyncTemplatesUseCase,
    private val deleteTemplateUseCase: DeleteTemplateUseCase
) : BaseViewModel<OutfitsState, OutfitsIntent, OutfitsEvent>(OutfitsState()) {

    private var searchJob: Job? = null
    private var outfitsJob: Job? = null
    private var templatesJob: Job? = null

    init { processIntent(OutfitsIntent.LoadInitialData) }

    override fun processIntent(intent: OutfitsIntent) {
        when (intent) {
            is OutfitsIntent.LoadInitialData -> loadInitialData()
            is OutfitsIntent.Refresh -> {
                loadTemplates()
                loadOutfits()
            }
            is OutfitsIntent.UpdateSearchQuery -> { updateState { copy(searchQuery = intent.query) }; debounceSearch() }
            is OutfitsIntent.SelectCategory -> { updateState { copy(selectedCategoryId = intent.categoryId) }; loadOutfits() }
            is OutfitsIntent.DeleteOutfit -> deleteOutfit(intent.outfitId)
            is OutfitsIntent.ShowOutfitDetail -> updateState { copy(selectedOutfitForDetail = intent.outfit) }
            is OutfitsIntent.SetEditMetaModalVisible -> updateState { copy(showEditMetaModal = intent.isVisible) }
            is OutfitsIntent.UpdateOutfitMeta -> updateMeta(intent)

            is OutfitsIntent.SetTemplateDialogVisible -> updateState { copy(showTemplateDialog = intent.isVisible) }
            is OutfitsIntent.DeleteTemplate -> deleteTemplate(intent.templateId)
        }
    }

    private fun loadInitialData() {
        syncCategoriesUseCase().onEach { domainList ->
            val uiCategories = domainList.filter { it.isOutfitCategory }.map { it.cherrymooncake_wardrobe_android_feature_common_presentation_ui_toUi() }
            updateState { copy(categories = uiCategories) }
        }.launchIn(viewModelScope)

        loadTemplates()
        loadOutfits()
    }

    private fun loadTemplates() {
        templatesJob?.cancel()
        templatesJob = syncTemplatesUseCase().onEach { domainList ->
            updateState { copy(templates = domainList.map { it.toUi() }) }
        }.launchIn(viewModelScope)
    }

    private fun loadOutfits() {
        outfitsJob?.cancel()
        updateState { copy(isLoading = true) }

        outfitsJob = syncOutfitsUseCase(
            searchTerm = state.value.searchQuery.takeIf { it.isNotBlank() },
            categoryId = state.value.selectedCategoryId
        ).onEach { domainList ->
            updateState { copy(outfits = domainList.map { it.toUi() }, isLoading = false) }
        }.catch { error ->
            updateState { copy(isLoading = false) }
            sendEvent(OutfitsEvent.ShowSnackbar("Ошибка: ${error.localizedMessage}"))
        }.launchIn(viewModelScope)
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch { delay(300); loadOutfits() }
    }

    private fun deleteOutfit(outfitId: String) {
        viewModelScope.launch {
            try { deleteOutfitUseCase(outfitId); sendEvent(OutfitsEvent.ShowSnackbar("Образ удален")); loadOutfits() }
            catch (e: Exception) { sendEvent(OutfitsEvent.ShowSnackbar("Ошибка: ${e.localizedMessage}")) }
        }
    }

    private fun updateMeta(intent: OutfitsIntent.UpdateOutfitMeta) {
        val currentOutfit = state.value.selectedOutfitForDetail ?: return
        viewModelScope.launch {
            updateState { copy(isEditingMeta = true) }
            try {
                val payloadItems = currentOutfit.items.filter { it.itemId != null }.map {
                    com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitItemPayloadDomainModel(
                        itemId = it.itemId!!, x = it.x, y = it.y, scale = it.scale, rotation = it.rotation, zIndex = it.zIndex
                    )
                }
                updateOutfitUseCase(intent.id, intent.name, intent.desc, currentOutfit.canvasWidth, currentOutfit.canvasHeight, null, payloadItems, intent.categoryIds)
                updateState { copy(showEditMetaModal = false) }
                sendEvent(OutfitsEvent.ShowSnackbar("Свойства обновлены"))
                loadOutfits()
            } catch (e: Exception) {
                sendEvent(OutfitsEvent.ShowSnackbar("Ошибка: ${e.localizedMessage}"))
            } finally { updateState { copy(isEditingMeta = false) } }
        }
    }

    private fun deleteTemplate(templateId: String) {
        viewModelScope.launch {
            try {
                deleteTemplateUseCase(templateId)
                sendEvent(OutfitsEvent.ShowSnackbar("Шаблон удален"))
                loadTemplates()
            } catch (e: Exception) {
                sendEvent(OutfitsEvent.ShowSnackbar("Ошибка удаления шаблона"))
            }
        }
    }
}

private fun com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel.cherrymooncake_wardrobe_android_feature_common_presentation_ui_toUi(): com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel {
    return com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel(this.id, this.name)
}