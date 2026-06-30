package com.cherrymooncake.wardrobe_android.feature.templates.data.api

import com.cherrymooncake.wardrobe_android.feature.templates.data.model.CreateTemplateApiModel
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.TemplateApiModel
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.UpdateTemplateApiModel
import retrofit2.http.*

interface ITemplatesApi {
    @GET("templates")
    suspend fun getTemplates(): List<TemplateApiModel>

    @GET("templates/{id}")
    suspend fun getTemplateById(@Path("id") id: String): TemplateApiModel

    @POST("templates")
    suspend fun createTemplate(@Body body: CreateTemplateApiModel): TemplateApiModel

    @PUT("templates/{id}")
    suspend fun updateTemplate(
        @Path("id") id: String,
        @Body body: UpdateTemplateApiModel
    ): TemplateApiModel

    @DELETE("templates/{id}")
    suspend fun deleteTemplate(@Path("id") id: String)
}