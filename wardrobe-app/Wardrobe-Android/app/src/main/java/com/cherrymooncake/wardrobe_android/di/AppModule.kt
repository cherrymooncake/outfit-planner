package com.cherrymooncake.wardrobe_android.di

import android.content.Context
import android.content.SharedPreferences
import com.cherrymooncake.wardrobe_android.core.network.source.ITokenLocalSource
import com.cherrymooncake.wardrobe_android.core.network.source.TokenLocalSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("wardrobe_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenLocalSource(sharedPreferences: SharedPreferences): ITokenLocalSource {
        return TokenLocalSourceImpl(sharedPreferences)
    }
}