package com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncOutfitsUseCase(
    private val remoteRepo: IOutfitsRemoteRepository,
    private val localRepo: IOutfitsLocalRepository
) {
    operator fun invoke(searchTerm: String? = null, categoryId: String? = null): Flow<List<OutfitDomainModel>> = flow {
        val hasFilters = !searchTerm.isNullOrBlank() || categoryId != null
        if (!hasFilters) {
            val local = localRepo.getOutfits()
            if (local.isNotEmpty()) emit(local)
        }
        try {
            val remote = remoteRepo.getOutfits(searchTerm, categoryId)
            if (!hasFilters) localRepo.saveOutfits(remote)
            emit(remote)
        } catch (e: Exception) {
            val local = localRepo.getOutfits()
            val filtered = local.filter { o ->
                val matchSearch = searchTerm?.let { o.name.contains(it, ignoreCase = true) } ?: true
                val matchCat = categoryId?.let { id -> o.categories.any { it.id == id } } ?: true
                matchSearch && matchCat
            }
            emit(filtered)
        }
    }
}