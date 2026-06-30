package com.cherrymooncake.wardrobe_android.feature.common.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState
import com.cherrymooncake.wardrobe_android.feature.common.presentation.model.DictionaryCategoryUiModel
import com.cherrymooncake.wardrobe_android.feature.common.presentation.model.DictionaryTagUiModel

data class DictionaryState(
    val isLoading: Boolean = false,
    val categories: List<DictionaryCategoryUiModel> = emptyList(),
    val tags: List<DictionaryTagUiModel> = emptyList()
) : IUiState

sealed class DictionaryIntent : IUiIntent {
    object LoadData : DictionaryIntent()

    data class AddCategory(val name: String, val isOutfit: Boolean, val isItem: Boolean) : DictionaryIntent()
    data class UpdateCategory(val id: String, val name: String) : DictionaryIntent()
    data class DeleteCategory(val id: String) : DictionaryIntent()

    data class AddTag(val name: String, val isOutfit: Boolean, val isItem: Boolean) : DictionaryIntent()
    data class UpdateTag(val id: String, val name: String) : DictionaryIntent()
    data class DeleteTag(val id: String) : DictionaryIntent()
}

sealed class DictionaryEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : DictionaryEvent()
}