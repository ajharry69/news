package com.xently.news.di.modules

import com.xently.news.data.source.remote.ArticleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit

@Module
@InstallIn(ApplicationComponent::class)
object ServicesModule {
    @Provides
    fun provideArticleService(retrofit: Retrofit): ArticleService =
        retrofit.create(ArticleService::class.java)
}