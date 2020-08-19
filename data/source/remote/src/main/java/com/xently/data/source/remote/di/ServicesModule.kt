package com.xently.data.source.remote.di

import com.xently.data.source.remote.services.ArticleService
import com.xently.data.source.remote.services.CommentService
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

    @Provides
    fun provideCommentService(retrofit: Retrofit): CommentService =
        retrofit.create(CommentService::class.java)
}