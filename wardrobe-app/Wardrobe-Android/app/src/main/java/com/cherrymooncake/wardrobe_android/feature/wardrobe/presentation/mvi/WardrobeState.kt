package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.IUiState
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.ItemUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.TagUiModel

data class WardrobeState(
    val isLoading: Boolean = true,
    val items: List<ItemUiModel> = emptyList(),

    val categories: List<CategoryUiModel> = emptyList(),
    val tags: List<TagUiModel> = emptyList(),

    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val selectedTagId: String? = null,
    val showAddModal: Boolean = false,
    val isAddingItem: Boolean = false,
    val selectedItemForDetail: ItemUiModel? = null,
    val showEditModal: Boolean = false,
    val isEditingItem: Boolean = false
) : IUiState