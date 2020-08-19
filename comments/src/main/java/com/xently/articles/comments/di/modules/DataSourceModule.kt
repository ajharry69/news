package com.xently.articles.comments.di.modules

import com.xently.articles.comments.data.source.CommentsLocalDataSource
import com.xently.articles.comments.data.source.CommentsRemoteDataSource
import com.xently.articles.comments.data.source.ICommentsDataSource
import com.xently.articles.comments.di.qualifiers.LocalCommentsDataSource
import com.xently.articles.comments.di.qualifiers.RemoteCommentsDataSource
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.data.source.local.daos.CommentsDAO
import com.xently.data.source.remote.services.CommentService
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
    @LocalCommentsDataSource
    fun provideCommentsLocalDataSource(dao: CommentsDAO): ICommentsDataSource =
        CommentsLocalDataSource(dao)

    @Provides
    @Singleton
    @RemoteCommentsDataSource
    fun provideCommentsRemoteDataSource(
        service: CommentService,
        @IODispatcher
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): ICommentsDataSource = CommentsRemoteDataSource(service, ioDispatcher)
}