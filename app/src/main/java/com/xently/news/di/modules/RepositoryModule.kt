package com.xently.news.di.modules

import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.news.data.ArticleMediator
import com.xently.news.data.repository.ArticlesRepository
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.data.source.IArticleDataSource
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
object RepositoryModule {
    @Provides
    @Singleton
    fun provideArticlesRepository(
        @LocalArticlesDataSource
        local: IArticleDataSource,
        @RemoteArticlesDataSource
        remote: IArticleDataSource,
        mediator: ArticleMediator,
        @IODispatcher
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): IArticlesRepository = ArticlesRepository(local, remote, mediator, ioDispatcher)
}