package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask

import androidx.compose.ui.geometry.Offset
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent

sealed class MaskEditorIntent : IUiIntent {
    data class LoadItem(val itemId: String) : MaskEditorIntent()
    data class AddPoint(val point: Offset) : MaskEditorIntent()
    object ClearPoints : MaskEditorIntent()

    data class ApplyMask(
        val canvasWidth: Float,
        val canvasHeight: Float,
        val imageIntrinsicWidth: Float,
        val imageIntrinsicHeight: Float
    ) : MaskEditorIntent()

    object RestoreAuto : MaskEditorIntent()
}