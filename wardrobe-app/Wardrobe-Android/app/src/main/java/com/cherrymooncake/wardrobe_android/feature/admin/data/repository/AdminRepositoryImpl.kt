package com.cherrymooncake.wardrobe_android.feature.admin.data.repository

import com.cherrymooncake.wardrobe_android.feature.admin.data.api.IAdminApi
import com.cherrymooncake.wardrobe_android.feature.admin.data.model.ChangeRoleApiModel
import com.cherrymooncake.wardrobe_android.feature.admin.domain.model.*
import com.cherrymooncake.wardrobe_android.feature.admin.domain.repository.IAdminRepository
import okhttp3.ResponseBody

class AdminRepositoryImpl(private val api: IAdminApi) : IAdminRepository {
    override suspend fun getHealth() = api.getHealth().let {
        HealthStatusDomainModel(it.dbStatus, it.bgRemovalStatus, it.aiStylistStatus)
    }

    override suspend fun getGlobalStats() = api.getGlobalStats().let {
        GlobalStatsDomainModel(it.totalUsers, it.totalItems, it.totalOutfits, it.imagesFolderSizeMb)
    }

    override suspend fun getUsers() = api.getUsers().map {
        UserStatDomainModel(it.id, it.email, it.role, it.registeredAt, it.lastActiveAt, it.itemsCount, it.outfitsCount)
    }

    override suspend fun changeRole(userId: String, newRole: String) {
        api.changeRole(userId, ChangeRoleApiModel(newRole))
    }

    override suspend fun downloadBackup(type: String): ResponseBody {
        return api.downloadBackup(type)
    }
}