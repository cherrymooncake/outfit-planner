package com.cherrymooncake.wardrobe_android.di

import com.cherrymooncake.wardrobe_android.core.db.WardrobeDB
import com.cherrymooncake.wardrobe_android.feature.templates.data.api.ITemplatesApi
import com.cherrymooncake.wardrobe_android.feature.templates.data.db.TemplatesDao
import com.cherrymooncake.wardrobe_android.feature.templates.data.repository.*
import com.cherrymooncake.wardrobe_android.feature.templates.data.source.*
import com.cherrymooncake.wardrobe_android.feature.templates.domain.repository.*
import com.cherrymooncake.wardrobe_android.feature.templates.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TemplatesModule {

    @Provides
    @Singleton
    fun provideTemplatesApi(retrofit: Retrofit): ITemplatesApi = retrofit.create(ITemplatesApi::class.java)

    @Provides
    @Singleton
    fun provideTemplatesDao(db: WardrobeDB): TemplatesDao = db.templatesDao()

    @Provides
    @Singleton
    fun provideTemplatesRemoteSource(api: ITemplatesApi): ITemplatesRemoteSource = TemplatesRemoteSourceImpl(api)

    @Provides
    @Singleton
    fun provideTemplatesLocalSource(dao: TemplatesDao): ITemplatesLocalSource = TemplatesLocalSourceImpl(dao)

    @Provides
    @Singleton
    fun provideTemplatesRemoteRepository(src: ITemplatesRemoteSource): ITemplatesRemoteRepository = TemplatesRemoteRepositoryImpl(src)

    @Provides
    @Singleton
    fun provideTemplatesLocalRepository(src: ITemplatesLocalSource): ITemplatesLocalRepository = TemplatesLocalRepositoryImpl(src)

    @Provides
    fun provideSyncTemplatesUseCase(remote: ITemplatesRemoteRepository, local: ITemplatesLocalRepository) = SyncTemplatesUseCase(remote, local)

    @Provides
    fun provideGetTemplateByIdUseCase(remote: ITemplatesRemoteRepository, local: ITemplatesLocalRepository) = GetTemplateByIdUseCase(remote, local)

    @Provides
    fun provideCreateTemplateUseCase(remote: ITemplatesRemoteRepository, local: ITemplatesLocalRepository) = CreateTemplateUseCase(remote, local)

    @Provides
    fun provideUpdateTemplateUseCase(remote: ITemplatesRemoteRepository, local: ITemplatesLocalRepository) = UpdateTemplateUseCase(remote, local)

    @Provides
    fun provideDeleteTemplateUseCase(remote: ITemplatesRemoteRepository, local: ITemplatesLocalRepository) = DeleteTemplateUseCase(remote, local)
}