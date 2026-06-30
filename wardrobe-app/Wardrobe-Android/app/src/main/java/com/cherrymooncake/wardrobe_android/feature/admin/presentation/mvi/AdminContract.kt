package com.cherrymooncake.wardrobe_android.feature.admin.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState
import com.cherrymooncake.wardrobe_android.feature.admin.domain.model.*

data class AdminState(
    val isLoading: Boolean = true,
    val health: HealthStatusDomainModel? = null,
    val stats: GlobalStatsDomainModel? = null,
    val users: List<UserStatDomainModel> = emptyList(),
    val isDownloading: Boolean = false
) : IUiState

sealed class AdminIntent : IUiIntent {
    object LoadData : AdminIntent()
    data class ChangeRole(val userId: String, val currentRole: String) : AdminIntent()
    data class SaveBackupToFile(val uri: android.net.Uri, val type: String, val context: android.content.Context) : AdminIntent()
}

sealed class AdminEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : AdminEvent()
}