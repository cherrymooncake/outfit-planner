package com.cherrymooncake.wardrobe_android.feature.common.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonLocalRepository
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncCategoriesUseCase(
    private val remoteRepository: ICommonRemoteRepository,
    private val localRepository: ICommonLocalRepository
) {
    operator fun invoke(): Flow<List<CategoryDomainModel>> = flow {
        val localData = localRepository.getCategories()
        if (localData.isNotEmpty()) {
            emit(localData)
        }

        try {
            val remoteData = remoteRepository.getCategories()
            localRepository.saveCategories(remoteData)
            emit(remoteData)
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(emptyList())
            }
        }
    }
}