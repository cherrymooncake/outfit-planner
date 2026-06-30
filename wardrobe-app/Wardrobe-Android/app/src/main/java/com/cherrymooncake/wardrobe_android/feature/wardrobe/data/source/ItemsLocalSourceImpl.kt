package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source

import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.db.ItemsDao
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemDbModel

class ItemsLocalSourceImpl(
    private val itemsDao: ItemsDao
) : IItemsLocalSource {

    override suspend fun getItems(): List<ItemDbModel> {
        return itemsDao.getAllItems()
    }

    override suspend fun saveItems(items: List<ItemDbModel>) {
        itemsDao.replaceAll(items)
    }

    override suspend fun saveItem(item: ItemDbModel) {
        itemsDao.insertItem(item)
    }

    override suspend fun deleteItem(itemId: String) {
        itemsDao.deleteItem(itemId)
    }

    override suspend fun getItemById(itemId: String): ItemDbModel? {
        return itemsDao.getItemById(itemId)
    }
}