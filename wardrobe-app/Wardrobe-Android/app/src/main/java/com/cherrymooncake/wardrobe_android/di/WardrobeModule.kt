package com.cherrymooncake.wardrobe_android.di

import com.cherrymooncake.wardrobe_android.core.db.WardrobeDB
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.api.IItemsApi
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.db.ItemsDao
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.repository.ItemsLocalRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.repository.ItemsRemoteRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source.IItemsLocalSource
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source.IItemsRemoteSource
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source.ItemsLocalSourceImpl
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source.ItemsRemoteSourceImpl
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.AddItemUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.DeleteItemUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.GetItemByIdUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.ReprocessMaskUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.RestoreAutoMaskUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.SyncItemsUseCase
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase.UpdateItemUseCase

@Module
@InstallIn(SingletonComponent::class)
object WardrobeModule {

    @Provides
    @Singleton
    fun provideItemsApi(retrofit: Retrofit): IItemsApi {
        return retrofit.create(IItemsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideItemsDao(db: WardrobeDB): ItemsDao {
        return db.itemsDao()
    }

    @Provides
    @Singleton
    fun provideItemsRemoteSource(api: IItemsApi): IItemsRemoteSource {
        return ItemsRemoteSourceImpl(api)
    }

    @Provides
    @Singleton
    fun provideItemsLocalSource(dao: ItemsDao): IItemsLocalSource {
        return ItemsLocalSourceImpl(dao)
    }

    @Provides
    @Singleton
    fun provideItemsRemoteRepository(
        remoteSource: IItemsRemoteSource
    ): IItemsRemoteRepository {
        return ItemsRemoteRepositoryImpl(remoteSource)
    }

    @Provides
    @Singleton
    fun provideItemsLocalRepository(
        localSource: IItemsLocalSource
    ): IItemsLocalRepository {
        return ItemsLocalRepositoryImpl(localSource)
    }

    @Provides
    fun provideSyncItemsUseCase(
        remoteRepository: IItemsRemoteRepository,
        localRepository: IItemsLocalRepository
    ): SyncItemsUseCase {
        return SyncItemsUseCase(remoteRepository, localRepository)
    }

    @Provides
    fun provideAddItemUseCase(
        remoteRepository: IItemsRemoteRepository,
        localRepository: IItemsLocalRepository
    ): AddItemUseCase {
        return AddItemUseCase(remoteRepository, localRepository)
    }

    @Provides
    fun provideUpdateItemUseCase(
        remoteRepository: IItemsRemoteRepository,
        localRepository: IItemsLocalRepository
    ): UpdateItemUseCase {
        return UpdateItemUseCase(remoteRepository, localRepository)
    }

    @Provides
    fun provideDeleteItemUseCase(
        remoteRepository: IItemsRemoteRepository,
        localRepository: IItemsLocalRepository
    ): DeleteItemUseCase {
        return DeleteItemUseCase(remoteRepository, localRepository)
    }

    @Provides
    fun provideGetItemByIdUseCase(
        localRepository: IItemsLocalRepository
    ): GetItemByIdUseCase {
        return GetItemByIdUseCase(localRepository)
    }

    @Provides
    fun provideReprocessMaskUseCase(
        remoteRepository: IItemsRemoteRepository,
        localRepository: IItemsLocalRepository
    ): ReprocessMaskUseCase {
        return ReprocessMaskUseCase(remoteRepository, localRepository)
    }

    @Provides
    fun provideRestoreAutoMaskUseCase(
        remoteRepository: IItemsRemoteRepository,
        localRepository: IItemsLocalRepository
    ): RestoreAutoMaskUseCase {
        return RestoreAutoMaskUseCase(remoteRepository, localRepository)
    }
}