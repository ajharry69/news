package com.xently.news.di.modules.storage

import android.content.Context
import androidx.room.Room
import com.xently.news.data.source.local.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object StorageModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return Room.databaseBuilder(context.applicationContext, NewsDatabase::class.java, "news.db")
            .fallbackToDestructiveMigration().build()
    }
}