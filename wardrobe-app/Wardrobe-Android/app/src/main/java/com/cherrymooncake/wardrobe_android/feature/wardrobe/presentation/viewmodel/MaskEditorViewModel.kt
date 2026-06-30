package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.GetItemByIdUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.ReprocessMaskUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.RestoreAutoMaskUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mapper.toUi
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask.MaskEditorEvent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask.MaskEditorIntent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask.MaskEditorState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaskEditorViewModel @Inject constructor(
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val reprocessMaskUseCase: ReprocessMaskUseCase,
    private val restoreAutoMaskUseCase: RestoreAutoMaskUseCase
) : BaseViewModel<MaskEditorState, MaskEditorIntent, MaskEditorEvent>(MaskEditorState()) {

    override fun processIntent(intent: MaskEditorIntent) {
        when (intent) {
            is MaskEditorIntent.LoadItem -> loadItem(intent.itemId)
            is MaskEditorIntent.AddPoint -> {
                val newPoints = state.value.points + intent.point
                updateState { copy(points = newPoints) }
            }
            is MaskEditorIntent.ClearPoints -> {
                updateState { copy(points = emptyList()) }
            }
            is MaskEditorIntent.ApplyMask -> applyMask(
                intent.canvasWidth,
                intent.canvasHeight,
                intent.imageIntrinsicWidth,
                intent.imageIntrinsicHeight
            )
            is MaskEditorIntent.RestoreAuto -> restoreAutoMask()
        }
    }

    private fun loadItem(itemId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, itemId = itemId) }
            val item = getItemByIdUseCase(itemId)
            if (item != null) {
                val uiItem = item.toUi()
                val cleanPath = item.originalImageUrl.removePrefix("/")
                val originalFullUrl = "http://localhost:5202/$cleanPath"

                updateState {
                    copy(
                        isLoading = false,
                        originalImageUrl = originalFullUrl
                    )
                }
            } else {
                updateState { copy(isLoading = false) }
                sendEvent(MaskEditorEvent.ShowSnackbar("Вещь не найдена"))
                sendEvent(MaskEditorEvent.NavigateBack)
            }
        }
    }

    private fun applyMask(canvasW: Float, canvasH: Float, imgW: Float, imgH: Float) {
        val currentPoints = state.value.points
        if (currentPoints.size < 3) {
            sendEvent(MaskEditorEvent.ShowSnackbar("Нарисуйте замкнутый контур (минимум 3 точки)"))
            return
        }

        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            try {
                val scaleX = imgW / canvasW
                val scaleY = imgH / canvasH

                val mappedPoints: List<List<Int>> = currentPoints.map { offset ->
                    val px = (offset.x * scaleX).toInt().coerceIn(0, imgW.toInt())
                    val py = (offset.y * scaleY).toInt().coerceIn(0, imgH.toInt())
                    listOf(px, py)
                }

                val contourJson = Gson().toJson(mappedPoints)

                reprocessMaskUseCase(state.value.itemId, contourJson)

                sendEvent(MaskEditorEvent.ShowSnackbar("Маска успешно применена!"))
                sendEvent(MaskEditorEvent.NavigateBack)
            } catch (e: Exception) {
                sendEvent(MaskEditorEvent.ShowSnackbar("Ошибка: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isSaving = false) }
            }
        }
    }

    private fun restoreAutoMask() {
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            try {
                restoreAutoMaskUseCase(state.value.itemId)
                sendEvent(MaskEditorEvent.ShowSnackbar("Авто-вырезка восстановлена!"))
                sendEvent(MaskEditorEvent.NavigateBack)
            } catch (e: Exception) {
                sendEvent(MaskEditorEvent.ShowSnackbar("Ошибка: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isSaving = false) }
            }
        }
    }
}