package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository
import java.io.File

class AddItemUseCase(
    private val remoteRepository: IItemsRemoteRepository,
    private val localRepository: IItemsLocalRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String?,
        imageFile: File,
        categoryIds: List<String>,
        tagIds: List<String>
    ) {
        val createdItem = remoteRepository.createItem(
            name = name,
            description = description,
            imageFile = imageFile,
            categoryIds = categoryIds,
            tagIds = tagIds
        )
        localRepository.saveItem(createdItem)
    }
}