package com.cherrymooncake.wardrobe_android.di

import android.content.Context
import androidx.room.Room
import com.cherrymooncake.wardrobe_android.core.db.WardrobeDB
import com.cherrymooncake.wardrobe_android.feature.common.data.db.CategoriesDao
import com.cherrymooncake.wardrobe_android.feature.common.data.db.TagsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWardrobeDatabase(
        @ApplicationContext context: Context
    ): WardrobeDB {
        return Room.databaseBuilder(
            context,
            WardrobeDB::class.java,
            "wardrobe_database"
        )

            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoriesDao(db: WardrobeDB): CategoriesDao {
        return db.categoriesDao()
    }

    @Provides
    @Singleton
    fun provideTagsDao(db: WardrobeDB): TagsDao {
        return db.tagsDao()
    }
}