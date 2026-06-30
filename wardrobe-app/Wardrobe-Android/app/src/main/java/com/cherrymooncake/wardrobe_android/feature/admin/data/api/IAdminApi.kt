package com.cherrymooncake.wardrobe_android.feature.admin.data.api

import com.cherrymooncake.wardrobe_android.feature.admin.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import okhttp3.ResponseBody
import retrofit2.http.Streaming

interface IAdminApi {
    @GET("admin/health")
    suspend fun getHealth(): HealthStatusApiModel

    @GET("admin/stats/global")
    suspend fun getGlobalStats(): GlobalStatsApiModel

    @GET("admin/users")
    suspend fun getUsers(): List<UserStatApiModel>

    @PUT("admin/users/{id}/role")
    suspend fun changeRole(@Path("id") id: String, @Body body: ChangeRoleApiModel)

    @Streaming
    @GET("admin/backup/{type}")
    suspend fun downloadBackup(@Path("type") type: String): ResponseBody

}