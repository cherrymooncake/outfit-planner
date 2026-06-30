package com.cherrymooncake.wardrobe_android.di

import com.cherrymooncake.wardrobe_android.core.db.WardrobeDB
import com.cherrymooncake.wardrobe_android.core.network.source.ITokenLocalSource
import com.cherrymooncake.wardrobe_android.feature.auth.data.api.IAuthApi
import com.cherrymooncake.wardrobe_android.feature.auth.data.repository.AuthLocalRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.auth.data.repository.AuthRemoteRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.auth.data.source.AuthRemoteSourceImpl
import com.cherrymooncake.wardrobe_android.feature.auth.data.source.IAuthRemoteSource
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthLocalRepository
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthRemoteRepository
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): IAuthApi = retrofit.create(IAuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRemoteSource(api: IAuthApi): IAuthRemoteSource = AuthRemoteSourceImpl(api)

    @Provides
    @Singleton
    fun provideAuthRemoteRepository(remoteSource: IAuthRemoteSource): IAuthRemoteRepository {
        return AuthRemoteRepositoryImpl(remoteSource)
    }

    @Provides
    @Singleton
    fun provideAuthLocalRepository(
        tokenLocalSource: ITokenLocalSource,
        db: WardrobeDB
    ): IAuthLocalRepository {
        return AuthLocalRepositoryImpl(tokenLocalSource, db)
    }

    @Provides
    fun provideLoginUseCase(remote: IAuthRemoteRepository, local: IAuthLocalRepository) = LoginUseCase(remote, local)

    @Provides
    fun provideRegisterUseCase(remote: IAuthRemoteRepository) = RegisterUseCase(remote)

    @Provides
    fun provideLogoutUseCase(local: IAuthLocalRepository) = LogoutUseCase(local)

    @Provides
    fun provideChangePasswordUseCase(remote: IAuthRemoteRepository) = ChangePasswordUseCase(remote)

    @Provides
    fun provideDeleteAccountUseCase(remote: IAuthRemoteRepository) = DeleteAccountUseCase(remote)
}