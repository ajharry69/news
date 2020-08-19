package com.xently.data.source.local.di

import com.xently.data.source.local.NewsDatabase
import com.xently.data.source.local.daos.ArticleDAO
import com.xently.data.source.local.daos.MediaDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object DAOsModule {
    @Provides
    fun provideArticlesDAO(database: NewsDatabase): ArticleDAO = database.articlesDAO

    @Provides
    fun provideMediaDAO(database: NewsDatabase): MediaDAO = database.mediaDAO
}