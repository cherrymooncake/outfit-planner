package com.cherrymooncake.wardrobe_android.di

import com.cherrymooncake.wardrobe_android.feature.ootd.data.api.IDailyOutfitsApi
import com.cherrymooncake.wardrobe_android.feature.ootd.data.api.IOpenMeteoApi
import com.cherrymooncake.wardrobe_android.feature.ootd.data.api.IWeatherApi
import com.cherrymooncake.wardrobe_android.feature.ootd.data.repository.DailyOutfitsRemoteRepositoryImpl
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.repository.IDailyOutfitsRemoteRepository
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DailyOutfitsModule {

    @Provides
    @Singleton
    fun provideDailyOutfitsApi(retrofit: Retrofit): IDailyOutfitsApi = retrofit.create(IDailyOutfitsApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): IWeatherApi = retrofit.create(IWeatherApi::class.java)

    @Provides
    @Singleton
    fun provideOpenMeteoApi(okHttpClient: OkHttpClient): IOpenMeteoApi {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IOpenMeteoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDailyOutfitsRemoteRepository(
        api: IDailyOutfitsApi,
        weatherApi: IWeatherApi,
        openMeteoApi: IOpenMeteoApi
    ): IDailyOutfitsRemoteRepository = DailyOutfitsRemoteRepositoryImpl(api, weatherApi, openMeteoApi)
    @Provides
    fun provideGetDailyOutfitUseCase(repo: IDailyOutfitsRemoteRepository) = GetDailyOutfitUseCase(repo)

    @Provides
    fun provideSearchCityUseCase(repo: IDailyOutfitsRemoteRepository) = SearchCityUseCase(repo)
    @Provides
    fun provideGetMonthOutfitsUseCase(repo: IDailyOutfitsRemoteRepository) = GetMonthOutfitsUseCase(repo)

    @Provides
    fun provideSetDailyOutfitUseCase(repo: IDailyOutfitsRemoteRepository) = SetDailyOutfitUseCase(repo)

    @Provides
    fun provideDeleteDailyOutfitUseCase(repo: IDailyOutfitsRemoteRepository) = DeleteDailyOutfitUseCase(repo)

    @Provides
    fun provideGetRandomOutfitUseCase(repo: IDailyOutfitsRemoteRepository) = GetRandomOutfitUseCase(repo)

    @Provides
    fun provideGetWeatherUseCase(repo: IDailyOutfitsRemoteRepository) = GetWeatherUseCase(repo)

    @Provides
    fun provideGetAiRecommendationUseCase(repo: IDailyOutfitsRemoteRepository) = GetAiRecommendationUseCase(repo)
}