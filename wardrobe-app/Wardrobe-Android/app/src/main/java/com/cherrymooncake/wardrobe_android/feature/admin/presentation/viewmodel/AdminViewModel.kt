package com.cherrymooncake.wardrobe_android.feature.admin.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.admin.domain.usecase.*
import com.cherrymooncake.wardrobe_android.feature.admin.presentation.mvi.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAdminDashboardUseCase: GetAdminDashboardUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val changeUserRoleUseCase: ChangeUserRoleUseCase,
    private val downloadBackupUseCase: DownloadBackupUseCase
) : BaseViewModel<AdminState, AdminIntent, AdminEvent>(AdminState()) {

    init { processIntent(AdminIntent.LoadData) }

    override fun processIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.LoadData -> loadData()
            is AdminIntent.ChangeRole -> changeRole(intent.userId, intent.currentRole)
            is AdminIntent.SaveBackupToFile -> saveBackup(intent.uri, intent.type, intent.context)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val (health, stats) = getAdminDashboardUseCase()
                val users = getUsersUseCase()
                updateState { copy(health = health, stats = stats, users = users, isLoading = false) }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendEvent(AdminEvent.ShowSnackbar("Ошибка загрузки данных"))
            }
        }
    }

    private fun changeRole(userId: String, currentRole: String) {
        viewModelScope.launch {
            try {
                val newRole = if (currentRole == "Admin") "User" else "Admin"
                changeUserRoleUseCase(userId, newRole)
                sendEvent(AdminEvent.ShowSnackbar("Роль успешно изменена"))
                processIntent(AdminIntent.LoadData)
            } catch (e: Exception) {
                sendEvent(AdminEvent.ShowSnackbar("Ошибка изменения роли"))
            }
        }
    }

    private fun saveBackup(uri: Uri, type: String, context: Context) {
        viewModelScope.launch {
            updateState { copy(isDownloading = true) }
            sendEvent(AdminEvent.ShowSnackbar("Скачивание началось..."))

            try {
                withContext(Dispatchers.IO) {
                    val responseBody = downloadBackupUseCase(type)

                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        responseBody.byteStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                sendEvent(AdminEvent.ShowSnackbar("Бэкап успешно сохранен!"))
            } catch (e: Exception) {
                sendEvent(AdminEvent.ShowSnackbar("Ошибка скачивания: ${e.localizedMessage}"))
            } finally {
                updateState { copy(isDownloading = false) }
            }
        }
    }
}