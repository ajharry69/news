package com.xently.news.di.modules

import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.news.data.source.IArticleDataSource
import com.xently.data.source.local.daos.ArticleDAO
import com.xently.news.data.source.ArticleLocalDataSource
import com.xently.data.source.local.daos.MediaDAO
import com.xently.news.data.source.ArticleRemoteDataSource
import com.xently.data.source.remote.services.ArticleService
import com.xently.news.di.qualifiers.LocalArticlesDataSource
import com.xently.news.di.qualifiers.RemoteArticlesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    @LocalArticlesDataSource
    fun provideArticlesLocalDataSource(dao: ArticleDAO, mediaDAO: MediaDAO): IArticleDataSource =
        ArticleLocalDataSource(dao, mediaDAO)

    @Provides
    @Singleton
    @RemoteArticlesDataSource
    fun provideArticlesRemoteDataSource(
        service: ArticleService,
        @IODispatcher
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): IArticleDataSource = ArticleRemoteDataSource(service, ioDispatcher)
}