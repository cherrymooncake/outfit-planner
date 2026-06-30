package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SyncItemsUseCaseTest {

    private lateinit var remoteRepo: IItemsRemoteRepository
    private lateinit var localRepo: IItemsLocalRepository
    private lateinit var syncItemsUseCase: SyncItemsUseCase

    @Before
    fun setup() {
        remoteRepo = mockk()
        localRepo = mockk(relaxed = true)
        syncItemsUseCase = SyncItemsUseCase(remoteRepo, localRepo)
    }

    @Test
    fun invoke_noFiltersAndNetworkSuccess_emitsLocalThenRemoteAndSaves() = runTest {
        val localItem = ItemDomainModel("1", "Local", null, "", "", emptyList(), emptyList())
        val remoteItem = ItemDomainModel("2", "Remote", null, "", "", emptyList(), emptyList())

        coEvery { localRepo.getItems() } returns listOf(localItem)
        coEvery { remoteRepo.getItems(null, null, null) } returns listOf(remoteItem)

        val result = syncItemsUseCase().toList()

        assertEquals(2, result.size)
        assertEquals("Local", result[0][0].name)
        assertEquals("Remote", result[1][0].name)

        coVerify { localRepo.saveItems(listOf(remoteItem)) }
    }

    @Test
    fun invoke_noFiltersAndNetworkFails_emitsLocalOnly() = runTest {
        val localItem = ItemDomainModel("1", "Local", null, "", "", emptyList(), emptyList())

        coEvery { localRepo.getItems() } returns listOf(localItem)
        coEvery { remoteRepo.getItems(null, null, null) } throws RuntimeException("Network error")

        val result = syncItemsUseCase().toList()

        assertEquals(2, result.size)
        assertEquals("Local", result[0][0].name)
        assertEquals("Local", result[1][0].name)

        coVerify(exactly = 0) { localRepo.saveItems(any()) }
    }

    @Test
    fun invoke_withFiltersAndNetworkSuccess_emitsRemoteDoesNotSave() = runTest {
        val remoteItem = ItemDomainModel("2", "RemoteSearch", null, "", "", emptyList(), emptyList())

        coEvery { remoteRepo.getItems("Search", null, null) } returns listOf(remoteItem)

        val result = syncItemsUseCase("Search", null, null).toList()

        assertEquals(1, result.size)
        assertEquals("RemoteSearch", result[0][0].name)

        coVerify(exactly = 0) { localRepo.saveItems(any()) }
    }

    @Test
    fun invoke_withFiltersAndNetworkFails_filtersLocalData() = runTest {
        val item1 = ItemDomainModel("1", "Red Shirt", null, "", "", emptyList(), emptyList())
        val item2 = ItemDomainModel("2", "Blue Pants", null, "", "", emptyList(), emptyList())

        coEvery { localRepo.getItems() } returns listOf(item1, item2)
        coEvery { remoteRepo.getItems("Red", null, null) } throws RuntimeException("Network error")

        val result = syncItemsUseCase("Red", null, null).toList()

        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals("Red Shirt", result[0][0].name)
    }
}