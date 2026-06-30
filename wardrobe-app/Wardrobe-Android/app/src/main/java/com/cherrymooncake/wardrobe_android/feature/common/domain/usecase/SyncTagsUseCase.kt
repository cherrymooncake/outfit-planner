package com.cherrymooncake.wardrobe_android.feature.common.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonLocalRepository
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncTagsUseCase(
    private val remoteRepository: ICommonRemoteRepository,
    private val localRepository: ICommonLocalRepository
) {
    operator fun invoke(): Flow<List<TagDomainModel>> = flow {
        val localData = localRepository.getTags()
        if (localData.isNotEmpty()) {
            emit(localData)
        }

        try {
            val remoteData = remoteRepository.getTags()
            localRepository.saveTags(remoteData)
            emit(remoteData)
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(emptyList())
            }
        }
    }
}