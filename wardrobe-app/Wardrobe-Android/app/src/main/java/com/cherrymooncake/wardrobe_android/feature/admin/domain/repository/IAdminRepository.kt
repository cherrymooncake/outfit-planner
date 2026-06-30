package com.cherrymooncake.wardrobe_android.feature.admin.domain.repository

import com.cherrymooncake.wardrobe_android.feature.admin.domain.model.*
import okhttp3.ResponseBody

interface IAdminRepository {
    suspend fun getHealth(): HealthStatusDomainModel
    suspend fun getGlobalStats(): GlobalStatsDomainModel
    suspend fun getUsers(): List<UserStatDomainModel>
    suspend fun changeRole(userId: String, newRole: String)
    suspend fun downloadBackup(type: String): ResponseBody
}