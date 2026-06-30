package com.cherrymooncake.wardrobe_android.feature.admin.data.model

import com.google.gson.annotations.SerializedName

data class HealthStatusApiModel(
    @SerializedName("dbStatus") val dbStatus: String,
    @SerializedName("bgRemovalStatus") val bgRemovalStatus: String,
    @SerializedName("aiStylistStatus") val aiStylistStatus: String
)

data class GlobalStatsApiModel(
    @SerializedName("totalUsers") val totalUsers: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("totalOutfits") val totalOutfits: Int,
    @SerializedName("imagesFolderSizeMb") val imagesFolderSizeMb: Double
)

data class UserStatApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("registeredAt") val registeredAt: String,
    @SerializedName("lastActiveAt") val lastActiveAt: String,
    @SerializedName("itemsCount") val itemsCount: Int,
    @SerializedName("outfitsCount") val outfitsCount: Int
)

data class ChangeRoleApiModel(
    @SerializedName("newRole") val newRole: String
)