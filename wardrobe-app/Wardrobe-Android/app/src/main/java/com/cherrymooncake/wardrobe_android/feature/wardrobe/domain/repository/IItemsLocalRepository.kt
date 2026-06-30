package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel

interface IItemsLocalRepository {
    suspend fun getItems(): List<ItemDomainModel>
    suspend fun saveItems(items: List<ItemDomainModel>)
    suspend fun saveItem(item: ItemDomainModel)
    suspend fun deleteItem(id: String)
    suspend fun getItemById(itemId: String): ItemDomainModel?
}