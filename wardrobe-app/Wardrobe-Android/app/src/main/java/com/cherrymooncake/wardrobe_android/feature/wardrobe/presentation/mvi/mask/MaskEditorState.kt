package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask

import androidx.compose.ui.geometry.Offset
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState

data class MaskEditorState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val itemId: String = "",
    val originalImageUrl: String = "",
    val points: List<Offset> = emptyList()
) : IUiState