package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel
import com.cherrymooncake.wardrobe_android.feature.templates.presentation.model.TemplateUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel

data class OutfitsState(
    val isLoading: Boolean = true,
    val outfits: List<OutfitUiModel> = emptyList(),
    val categories: List<CategoryUiModel> = emptyList(),

    val templates: List<TemplateUiModel> = emptyList(),
    val showTemplateDialog: Boolean = false,

    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val selectedOutfitForDetail: OutfitUiModel? = null,
    val showEditMetaModal: Boolean = false,
    val isEditingMeta: Boolean = false
) : IUiState

sealed class OutfitsIntent : IUiIntent {
    object LoadInitialData : OutfitsIntent()
    object Refresh : OutfitsIntent()
    data class UpdateSearchQuery(val query: String) : OutfitsIntent()
    data class SelectCategory(val categoryId: String?) : OutfitsIntent()
    data class DeleteOutfit(val outfitId: String) : OutfitsIntent()
    data class ShowOutfitDetail(val outfit: OutfitUiModel?) : OutfitsIntent()
    data class SetEditMetaModalVisible(val isVisible: Boolean) : OutfitsIntent()
    data class UpdateOutfitMeta(val id: String, val name: String, val desc: String?, val categoryIds: List<String>) : OutfitsIntent()

    data class SetTemplateDialogVisible(val isVisible: Boolean) : OutfitsIntent()
    data class DeleteTemplate(val templateId: String) : OutfitsIntent()
}

sealed class OutfitsEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : OutfitsEvent()
}