package com.cherrymooncake.wardrobe_android.feature.admin.domain.model

data class HealthStatusDomainModel(
    val dbStatus: String,
    val bgRemovalStatus: String,
    val aiStylistStatus: String
)
data class GlobalStatsDomainModel(val users: Int, val items: Int, val outfits: Int, val sizeMb: Double)

data class UserStatDomainModel(
    val id: String, val email: String, val role: String,
    val registeredAt: String, val lastActiveAt: String,
    val itemsCount: Int, val outfitsCount: Int
)