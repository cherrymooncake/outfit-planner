package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.api

import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ImageUrlResponseApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ManualMaskApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.UpdateItemApiModel
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IItemsApi {

    @GET("items")
    suspend fun getItems(
        @Query("SearchTerm") searchTerm: String? = null,
        @Query("CategoryId") categoryId: String? = null,
        @Query("TagId") tagId: String? = null
    ): List<ItemApiModel>

    @POST("items")
    suspend fun createItem(@Body body: MultipartBody): ItemApiModel

    @PUT("items/{id}")
    suspend fun updateItem(
        @Path("id") id: String,
        @Body body: UpdateItemApiModel
    ): ItemApiModel

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String)
    @POST("items/{id}/reprocess-mask")
    suspend fun reprocessMask(
        @Path("id") id: String,
        @Body body: ManualMaskApiModel
    ): ImageUrlResponseApiModel

    @POST("items/{id}/restore-auto")
    suspend fun restoreAutoMask(@Path("id") id: String): ImageUrlResponseApiModel
}