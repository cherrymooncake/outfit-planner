package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.usecase.SyncCategoriesUseCase
import com.cherrymooncake.wardrobe_android.feature.common.domain.usecase.SyncTagsUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.DeleteItemUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.SyncItemsUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mapper.toUi
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.WardrobeEvent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.WardrobeIntent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.WardrobeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.AddItemUseCase
import java.io.File
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.UpdateItemUseCase

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val syncItemsUseCase: SyncItemsUseCase,
    private val syncCategoriesUseCase: SyncCategoriesUseCase,
    private val syncTagsUseCase: SyncTagsUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val addItemUseCase: AddItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase
) : BaseViewModel<WardrobeState, WardrobeIntent, WardrobeEvent>(WardrobeState()) {

    private var searchJob: Job? = null
    private var itemsJob: Job? = null

    init {
        processIntent(WardrobeIntent.LoadInitialData)
    }

    override fun processIntent(intent: WardrobeIntent) {
        when (intent) {
            is WardrobeIntent.LoadInitialData -> loadInitialData()
            is WardrobeIntent.Refresh -> loadItems()
            is WardrobeIntent.UpdateSearchQuery -> {
                updateState { copy(searchQuery = intent.query) }
                debounceSearch()
            }
            is WardrobeIntent.SelectCategory -> {
                updateState { copy(selectedCategoryId = intent.categoryId) }
                loadItems()
            }
            is WardrobeIntent.SelectTag -> {
                updateState { copy(selectedTagId = intent.tagId) }
                loadItems()
            }
            is WardrobeIntent.DeleteItem -> deleteItem(intent.itemId)

            is WardrobeIntent.SetAddModalVisible -> {
                updateState { copy(showAddModal = intent.isVisible) }
            }
            is WardrobeIntent.AddItem -> addItem(
                intent.name, intent.description, intent.imageFile, intent.categoryIds, intent.tagIds
            )
            is WardrobeIntent.ShowItemDetail -> updateState { copy(selectedItemForDetail = intent.item) }
            is WardrobeIntent.SetEditModalVisible -> updateState { copy(showEditModal = intent.isVisible) }
            is WardrobeIntent.UpdateItem -> updateItem(intent.id, intent.name, intent.description, intent.categoryIds, intent.tagIds)
            is WardrobeIntent.ReloadDictionaries -> loadInitialData()
        }
    }

    private fun addItem(name: String, desc: String?, file: File, cats: List<String>, tags: List<String>) {
        viewModelScope.launch {
            updateState { copy(isAddingItem = true) }
            try {
                addItemUseCase(name, desc, file, cats, tags)
                updateState { copy(showAddModal = false) }
                sendEvent(WardrobeEvent.ShowSnackbar("Вещь успешно загружена! Вырезка фона может занять время."))
                loadItems()
            } catch (e: Exception) {
                sendEvent(WardrobeEvent.ShowSnackbar("Ошибка добавления: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isAddingItem = false) }
            }
        }
    }

    private fun updateItem(id: String, name: String, desc: String?, cats: List<String>, tags: List<String>) {
        viewModelScope.launch {
            updateState { copy(isEditingItem = true) }
            try {
                updateItemUseCase(id, name, desc, cats, tags)
                updateState { copy(showEditModal = false) }
                sendEvent(WardrobeEvent.ShowSnackbar("Вещь успешно обновлена!"))
                loadItems()
            } catch (e: Exception) {
                sendEvent(WardrobeEvent.ShowSnackbar("Ошибка обновления: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isEditingItem = false) }
            }
        }
    }

    private fun loadInitialData() {
        syncCategoriesUseCase()
            .onEach { domainList ->
                val uiCategories = domainList.filter { it.isItemCategory }.map { it.toUi() }
                updateState { copy(categories = uiCategories) }
            }
            .launchIn(viewModelScope)

        syncTagsUseCase()
            .onEach { domainList ->
                val uiTags = domainList.filter { it.isItemTag }.map { it.toUi() }
                updateState { copy(tags = uiTags) }
            }
            .launchIn(viewModelScope)

        loadItems()
    }

    private fun loadItems() {
        itemsJob?.cancel()
        updateState { copy(isLoading = true) }

        itemsJob = syncItemsUseCase(
            searchTerm = state.value.searchQuery.takeIf { it.isNotBlank() },
            categoryId = state.value.selectedCategoryId,
            tagId = state.value.selectedTagId
        ).onEach { domainItems ->
            val uiItems = domainItems.map { it.toUi() }


            val currentDetailId = state.value.selectedItemForDetail?.id
            val updatedDetailItem = uiItems.find { it.id == currentDetailId }

            updateState {
                copy(
                    items = uiItems,
                    isLoading = false,
                    selectedItemForDetail = updatedDetailItem ?: selectedItemForDetail
                )
            }
        }.catch { error ->
            updateState { copy(isLoading = false) }
            sendEvent(WardrobeEvent.ShowSnackbar("Ошибка загрузки: ${error.localizedMessage}"))
        }.launchIn(viewModelScope)
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            loadItems()
        }
    }

    private fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                deleteItemUseCase(itemId)
                sendEvent(WardrobeEvent.ShowSnackbar("Вещь успешно удалена"))
                loadItems()
            } catch (e: Exception) {
                sendEvent(WardrobeEvent.ShowSnackbar("Ошибка удаления: ${e.localizedMessage}"))
            }
        }
    }
}