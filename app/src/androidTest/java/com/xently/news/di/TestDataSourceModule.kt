package com.xently.news.di

import com.xently.news.ARTICLE
import com.xently.news.createArticles
import com.xently.news.data.source.IArticleDataSource
import com.xently.news.di.qualifiers.LocalArticlesDataSource
import com.xently.news.di.qualifiers.RemoteArticlesDataSource
import com.xently.news.fakes.FakeArticleDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object TestDataSourceModule {
    @Provides
    @Singleton
    @LocalArticlesDataSource
    fun provideLocalArticlesDataSource(): IArticleDataSource = FakeArticleDataSource()

    @Provides
    @Singleton
    @RemoteArticlesDataSource
    fun provideRemoteArticlesDataSource(): IArticleDataSource =
        FakeArticleDataSource(*createArticles(10).toTypedArray(), ARTICLE)
}