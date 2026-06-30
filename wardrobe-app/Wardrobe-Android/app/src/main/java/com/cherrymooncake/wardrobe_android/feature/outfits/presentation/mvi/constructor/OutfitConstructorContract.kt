package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mvi.constructor

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitItemUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.ItemUiModel

data class OutfitConstructorState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val outfitId: String? = null,
    val name: String = "",
    val description: String = "",
    val canvasWidth: Int = 800,
    val canvasHeight: Int = 600,

    val outfitCategoryIds: Set<String> = emptySet(),
    val outfitCategoriesDict: List<CategoryUiModel> = emptyList(),

    val canvasItems: List<OutfitItemUiModel> = emptyList(),
    val selectedCanvasItemId: String? = null,

    val wardrobeItems: List<ItemUiModel> = emptyList(),
    val wardrobeCategories: List<CategoryUiModel> = emptyList(),
    val wardrobeSelectedCategoryId: String? = null,

    val showSaveDialog: Boolean = false,
    val showCategoryManager: Boolean = false,

    val sourceTemplateId: String? = null,
    val isTemplateEditMode: Boolean = false,
    val showSaveTemplateDialog: Boolean = false,
    val isSavingTemplate: Boolean = false,
    val targetDate: String? = null
) : IUiState

sealed class OutfitConstructorIntent : IUiIntent {
    data class LoadInitialData(
        val outfitId: String?,
        val templateId: String? = null,
        val isTemplateEditMode: Boolean = false,
        val targetDate: String? = null
    ) : OutfitConstructorIntent()

    object ReloadDictionaries : OutfitConstructorIntent()

    data class AddItemToCanvas(val item: ItemUiModel) : OutfitConstructorIntent()
    data class UpdateItemTransform(val id: String, val x: Float, val y: Float, val scale: Float, val rotation: Float) : OutfitConstructorIntent()
    data class SelectCanvasItem(val id: String?) : OutfitConstructorIntent()
    data class RemoveCanvasItem(val id: String) : OutfitConstructorIntent()
    object BringForward : OutfitConstructorIntent()
    object SendBackward : OutfitConstructorIntent()

    data class UpdateCanvasSize(val width: Int, val height: Int) : OutfitConstructorIntent()
    data class SelectWardrobeCategory(val categoryId: String?) : OutfitConstructorIntent()

    data class SetSaveDialogVisible(val isVisible: Boolean) : OutfitConstructorIntent()
    data class SetCategoryManagerVisible(val isVisible: Boolean) : OutfitConstructorIntent()
    data class UpdateMetadata(val name: String, val description: String) : OutfitConstructorIntent()
    data class UpdateOutfitCategories(val categoryIds: Set<String>) : OutfitConstructorIntent()
    object SaveOutfit : OutfitConstructorIntent()

    data class SetSaveTemplateDialogVisible(val isVisible: Boolean) : OutfitConstructorIntent()
    data class SaveAsTemplate(val name: String, val description: String) : OutfitConstructorIntent()
}

sealed class OutfitConstructorEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : OutfitConstructorEvent()
    object NavigateBack : OutfitConstructorEvent()
}