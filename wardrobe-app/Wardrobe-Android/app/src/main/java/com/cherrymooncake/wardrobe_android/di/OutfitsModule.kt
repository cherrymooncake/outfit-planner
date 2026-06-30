package com.cherrymooncake.wardrobe_android.di

import com.cherrymooncake.wardrobe_android.core.db.WardrobeDB
import com.cherrymooncake.wardrobe_android.feature.outfits.data.api.IOutfitsApi
import com.cherrymooncake.wardrobe_android.feature.outfits.data.db.OutfitsDao
import com.cherrymooncake.wardrobe_android.feature.outfits.data.repository.*
import com.cherrymooncake.wardrobe_android.feature.outfits.data.source.*
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.*
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OutfitsModule {

    @Provides
    @Singleton
    fun provideOutfitsApi(retrofit: Retrofit): IOutfitsApi = retrofit.create(IOutfitsApi::class.java)

    @Provides
    @Singleton
    fun provideOutfitsDao(db: WardrobeDB): OutfitsDao = db.outfitsDao()

    @Provides
    @Singleton
    fun provideOutfitsRemoteSource(api: IOutfitsApi): IOutfitsRemoteSource = OutfitsRemoteSourceImpl(api)

    @Provides
    @Singleton
    fun provideOutfitsLocalSource(dao: OutfitsDao): IOutfitsLocalSource = OutfitsLocalSourceImpl(dao)

    @Provides
    @Singleton
    fun provideOutfitsRemoteRepository(src: IOutfitsRemoteSource): IOutfitsRemoteRepository = OutfitsRemoteRepositoryImpl(src)

    @Provides
    @Singleton
    fun provideOutfitsLocalRepository(src: IOutfitsLocalSource): IOutfitsLocalRepository = OutfitsLocalRepositoryImpl(src)

    @Provides
    fun provideSyncOutfitsUseCase(remote: IOutfitsRemoteRepository, local: IOutfitsLocalRepository) = SyncOutfitsUseCase(remote, local)

    @Provides
    fun provideGetOutfitByIdUseCase(remote: IOutfitsRemoteRepository, local: IOutfitsLocalRepository) = GetOutfitByIdUseCase(remote, local)

    @Provides
    fun provideCreateOutfitUseCase(remote: IOutfitsRemoteRepository, local: IOutfitsLocalRepository) = CreateOutfitUseCase(remote, local)

    @Provides
    fun provideUpdateOutfitUseCase(remote: IOutfitsRemoteRepository, local: IOutfitsLocalRepository) = UpdateOutfitUseCase(remote, local)

    @Provides
    fun provideDeleteOutfitUseCase(remote: IOutfitsRemoteRepository, local: IOutfitsLocalRepository) = DeleteOutfitUseCase(remote, local)
}