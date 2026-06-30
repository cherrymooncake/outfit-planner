package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncItemsUseCase(
    private val remoteRepository: IItemsRemoteRepository,
    private val localRepository: IItemsLocalRepository
) {
    operator fun invoke(
        searchTerm: String? = null,
        categoryId: String? = null,
        tagId: String? = null
    ): Flow<List<ItemDomainModel>> = flow {

        val hasFilters = !searchTerm.isNullOrBlank() || categoryId != null || tagId != null

        if (!hasFilters) {
            val localData = localRepository.getItems()
            if (localData.isNotEmpty()) {
                emit(localData)
            }
        }

        try {
            val remoteData = remoteRepository.getItems(searchTerm, categoryId, tagId)

            if (!hasFilters) {
                localRepository.saveItems(remoteData)
            }

            emit(remoteData)

        } catch (e: Exception) {
            val localData = localRepository.getItems()

            val filteredData = localData.filter { item ->
                val matchSearch = searchTerm?.let { item.name.contains(it, ignoreCase = true) } ?: true
                val matchCat = categoryId?.let { id -> item.categories.any { it.id == id } } ?: true
                val matchTag = tagId?.let { id -> item.tags.any { it.id == id } } ?: true

                matchSearch && matchCat && matchTag
            }

            emit(filteredData)
        }
    }
}