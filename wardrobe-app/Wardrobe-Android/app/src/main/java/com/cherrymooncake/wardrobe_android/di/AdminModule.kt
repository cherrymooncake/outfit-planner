package com.cherrymooncake.wardrobe_android.di

import com.cherrymooncake.wardrobe_android.feature.admin.data.api.IAdminApi
import com.cherrymooncake.wardrobe_android.feature.admin.data.repository.AdminRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.admin.domain.repository.IAdminRepository
import com.cherrymooncake.wardrobe_android.feature.admin.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminModule {

    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): IAdminApi = retrofit.create(IAdminApi::class.java)

    @Provides
    @Singleton
    fun provideAdminRepository(api: IAdminApi): IAdminRepository = AdminRepositoryImpl(api)

    @Provides
    fun provideGetAdminDashboardUseCase(repo: IAdminRepository) = GetAdminDashboardUseCase(repo)

    @Provides
    fun provideGetUsersUseCase(repo: IAdminRepository) = GetUsersUseCase(repo)

    @Provides
    fun provideChangeUserRoleUseCase(repo: IAdminRepository) = ChangeUserRoleUseCase(repo)

    @Provides
    fun provideDownloadBackupUseCase(repo: IAdminRepository) = DownloadBackupUseCase(repo)
}