package com.cherrymooncake.wardrobe_android.feature.auth.data.mapper

import com.cherrymooncake.wardrobe_android.feature.auth.data.model.TokenApiModel
import com.cherrymooncake.wardrobe_android.feature.auth.domain.model.TokenDomainModel

fun TokenApiModel.toDomain(): TokenDomainModel {
    return TokenDomainModel(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}