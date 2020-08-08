package com.xently.news.di.modules

import com.xently.news.data.source.IArticleDataSource
import com.xently.news.data.source.local.ArticleDAO
import com.xently.news.data.source.local.ArticleLocalDataSource
import com.xently.news.data.source.remote.ArticleRemoteDataSource
import com.xently.news.data.source.remote.ArticleService
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
    fun provideArticlesLocalDataSource(dao: ArticleDAO): IArticleDataSource =
        ArticleLocalDataSource(dao)

    @Provides
    @Singleton
    @RemoteArticlesDataSource
    fun provideArticlesRemoteDataSource(
        service: ArticleService,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): IArticleDataSource = ArticleRemoteDataSource(service, ioDispatcher)
}