package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.ItemUiModel
import java.io.File

sealed class WardrobeIntent : IUiIntent {
    object LoadInitialData : WardrobeIntent()
    object Refresh : WardrobeIntent()

    data class UpdateSearchQuery(val query: String) : WardrobeIntent()
    data class SelectCategory(val categoryId: String?) : WardrobeIntent()
    data class SelectTag(val tagId: String?) : WardrobeIntent()

    data class DeleteItem(val itemId: String) : WardrobeIntent()

    data class SetAddModalVisible(val isVisible: Boolean) : WardrobeIntent()

    data class ShowItemDetail(val item: ItemUiModel?) : WardrobeIntent()
    data class AddItem(
        val name: String,
        val description: String?,
        val imageFile: File,
        val categoryIds: List<String>,
        val tagIds: List<String>
    ) : WardrobeIntent()

    data class SetEditModalVisible(val isVisible: Boolean) : WardrobeIntent()
    data class UpdateItem(
        val id: String,
        val name: String,
        val description: String?,
        val categoryIds: List<String>,
        val tagIds: List<String>
    ) : WardrobeIntent()

    object ReloadDictionaries : WardrobeIntent()
}