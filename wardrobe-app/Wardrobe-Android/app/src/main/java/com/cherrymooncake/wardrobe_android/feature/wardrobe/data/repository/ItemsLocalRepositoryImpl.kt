package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.repository

import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.mapper.toDbModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source.IItemsLocalSource
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository

class ItemsLocalRepositoryImpl(
    private val localSource: IItemsLocalSource
) : IItemsLocalRepository {

    override suspend fun getItems(): List<ItemDomainModel> {
        return localSource.getItems().map { it.toDomain() }
    }

    override suspend fun saveItems(items: List<ItemDomainModel>) {
        localSource.saveItems(items.map { it.toDbModel() })
    }

    override suspend fun saveItem(item: ItemDomainModel) {
        localSource.saveItem(item.toDbModel())
    }

    override suspend fun deleteItem(id: String) {
        localSource.deleteItem(id)
    }

    override suspend fun getItemById(itemId: String): ItemDomainModel? {
        return localSource.getItemById(itemId)?.toDomain()
    }
}