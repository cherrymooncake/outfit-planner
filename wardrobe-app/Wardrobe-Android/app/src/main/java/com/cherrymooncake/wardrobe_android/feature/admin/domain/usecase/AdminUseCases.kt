package com.cherrymooncake.wardrobe_android.feature.admin.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.admin.domain.repository.IAdminRepository
import okhttp3.ResponseBody

class GetAdminDashboardUseCase(private val repo: IAdminRepository) {
    suspend operator fun invoke() = Pair(repo.getHealth(), repo.getGlobalStats())
}

class GetUsersUseCase(private val repo: IAdminRepository) {
    suspend operator fun invoke() = repo.getUsers()
}

class ChangeUserRoleUseCase(private val repo: IAdminRepository) {
    suspend operator fun invoke(id: String, newRole: String) = repo.changeRole(id, newRole)
}

class DownloadBackupUseCase(private val repo: IAdminRepository) {
    suspend operator fun invoke(type: String): ResponseBody = repo.downloadBackup(type)
}