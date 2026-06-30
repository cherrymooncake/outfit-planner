package com.cherrymooncake.wardrobe_android.feature.common.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.usecase.*
import com.cherrymooncake.wardrobe_android.feature.common.presentation.model.toDictionaryUi
import com.cherrymooncake.wardrobe_android.feature.common.presentation.mvi.DictionaryEvent
import com.cherrymooncake.wardrobe_android.feature.common.presentation.mvi.DictionaryIntent
import com.cherrymooncake.wardrobe_android.feature.common.presentation.mvi.DictionaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val syncCategoriesUseCase: SyncCategoriesUseCase,
    private val syncTagsUseCase: SyncTagsUseCase,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : BaseViewModel<DictionaryState, DictionaryIntent, DictionaryEvent>(DictionaryState()) {

    init {
        processIntent(DictionaryIntent.LoadData)
    }

    override fun processIntent(intent: DictionaryIntent) {
        when (intent) {
            is DictionaryIntent.LoadData -> loadData()

            is DictionaryIntent.AddCategory -> handleRequest { createCategoryUseCase(intent.name, intent.isOutfit, intent.isItem) }
            is DictionaryIntent.UpdateCategory -> handleRequest { updateCategoryUseCase(intent.id, intent.name) }
            is DictionaryIntent.DeleteCategory -> handleRequest { deleteCategoryUseCase(intent.id) }

            is DictionaryIntent.AddTag -> handleRequest { createTagUseCase(intent.name, intent.isOutfit, intent.isItem) }
            is DictionaryIntent.UpdateTag -> handleRequest { updateTagUseCase(intent.id, intent.name) }
            is DictionaryIntent.DeleteTag -> handleRequest { deleteTagUseCase(intent.id) }
        }
    }

    private fun loadData() {
        syncCategoriesUseCase().onEach { domainList ->
            updateState { copy(categories = domainList.map { it.toDictionaryUi() }) }
        }.launchIn(viewModelScope)

        syncTagsUseCase().onEach { domainList ->
            updateState { copy(tags = domainList.map { it.toDictionaryUi() }) }
        }.launchIn(viewModelScope)
    }

    private fun handleRequest(action: suspend () -> Unit) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                action()
                loadData()
            } catch (e: Exception) {
                sendEvent(DictionaryEvent.ShowSnackbar("Ошибка: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isLoading = false) }
            }
        }
    }
}