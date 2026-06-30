package com.cherrymooncake.wardrobe_android.di

import com.cherrymooncake.wardrobe_android.feature.common.data.api.ICommonApi
import com.cherrymooncake.wardrobe_android.feature.common.data.db.CategoriesDao
import com.cherrymooncake.wardrobe_android.feature.common.data.db.TagsDao
import com.cherrymooncake.wardrobe_android.feature.common.data.repository.CommonLocalRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.common.data.repository.CommonRemoteRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.common.data.source.CommonLocalSourceImpl
import com.cherrymooncake.wardrobe_android.feature.common.data.source.CommonRemoteSourceImpl
import com.cherrymooncake.wardrobe_android.feature.common.data.source.ICommonLocalSource
import com.cherrymooncake.wardrobe_android.feature.common.data.source.ICommonRemoteSource
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonLocalRepository
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonRemoteRepository
import com.cherrymooncake.wardrobe_android.feature.common.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    fun provideCommonApi(retrofit: Retrofit): ICommonApi {
        return retrofit.create(ICommonApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCommonRemoteSource(api: ICommonApi): ICommonRemoteSource {
        return CommonRemoteSourceImpl(api)
    }

    @Provides
    @Singleton
    fun provideCommonRemoteRepository(
        remoteSource: ICommonRemoteSource
    ): ICommonRemoteRepository {
        return CommonRemoteRepositoryImpl(remoteSource)
    }

    @Provides
    @Singleton
    fun provideCommonLocalSource(
        categoriesDao: CategoriesDao,
        tagsDao: TagsDao
    ): ICommonLocalSource {
        return CommonLocalSourceImpl(categoriesDao, tagsDao)
    }

    @Provides
    @Singleton
    fun provideCommonLocalRepository(
        localSource: ICommonLocalSource
    ): ICommonLocalRepository {
        return CommonLocalRepositoryImpl(localSource)
    }

    @Provides
    fun provideSyncCategoriesUseCase(
        remoteRepository: ICommonRemoteRepository,
        localRepository: ICommonLocalRepository
    ): SyncCategoriesUseCase {
        return SyncCategoriesUseCase(remoteRepository, localRepository)
    }

    @Provides
    fun provideSyncTagsUseCase(
        remoteRepository: ICommonRemoteRepository,
        localRepository: ICommonLocalRepository
    ): SyncTagsUseCase {
        return SyncTagsUseCase(remoteRepository, localRepository)
    }

    @Provides
    fun provideCreateCategoryUseCase(repo: ICommonRemoteRepository) = CreateCategoryUseCase(repo)

    @Provides
    fun provideUpdateCategoryUseCase(repo: ICommonRemoteRepository) = UpdateCategoryUseCase(repo)

    @Provides
    fun provideDeleteCategoryUseCase(repo: ICommonRemoteRepository) = DeleteCategoryUseCase(repo)

    @Provides
    fun provideCreateTagUseCase(repo: ICommonRemoteRepository) = CreateTagUseCase(repo)

    @Provides
    fun provideUpdateTagUseCase(repo: ICommonRemoteRepository) = UpdateTagUseCase(repo)

    @Provides
    fun provideDeleteTagUseCase(repo: ICommonRemoteRepository) = DeleteTagUseCase(repo)
}