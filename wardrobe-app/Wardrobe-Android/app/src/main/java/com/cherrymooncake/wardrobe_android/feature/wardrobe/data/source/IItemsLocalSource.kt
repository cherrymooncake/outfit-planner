package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source

import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemDbModel

interface IItemsLocalSource {
    suspend fun getItems(): List<ItemDbModel>
    suspend fun saveItems(items: List<ItemDbModel>)
    suspend fun saveItem(item: ItemDbModel)
    suspend fun deleteItem(itemId: String)
    suspend fun getItemById(itemId: String): ItemDbModel?
}