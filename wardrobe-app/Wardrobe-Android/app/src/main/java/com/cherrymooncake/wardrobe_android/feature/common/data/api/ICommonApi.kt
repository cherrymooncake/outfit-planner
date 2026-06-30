package com.cherrymooncake.wardrobe_android.feature.common.data.api

import com.cherrymooncake.wardrobe_android.feature.common.data.model.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ICommonApi {
    @GET("categories")
    suspend fun getCategories(): List<CategoryApiModel>

    @POST("categories")
    suspend fun createCategory(@Body body: CreateCategoryApiModel): CategoryApiModel

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body body: UpdateCategoryApiModel): CategoryApiModel

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String)


    @GET("tags")
    suspend fun getTags(): List<TagApiModel>

    @POST("tags")
    suspend fun createTag(@Body body: CreateTagApiModel): TagApiModel

    @PUT("tags/{id}")
    suspend fun updateTag(@Path("id") id: String, @Body body: UpdateTagApiModel): TagApiModel

    @DELETE("tags/{id}")
    suspend fun deleteTag(@Path("id") id: String)
}