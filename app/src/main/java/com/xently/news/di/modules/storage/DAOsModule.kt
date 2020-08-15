package com.xently.news.di.modules.storage

import com.xently.news.data.source.local.ArticleDAO
import com.xently.news.data.source.local.MediaDAO
import com.xently.news.data.source.local.NewsDatabase
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