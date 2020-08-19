package com.xently.articles.comments.di.modules

import com.xently.articles.comments.data.repository.CommentsRepository
import com.xently.articles.comments.data.repository.ICommentsRepository
import com.xently.articles.comments.data.source.ICommentsDataSource
import com.xently.articles.comments.di.qualifiers.LocalCommentsDataSource
import com.xently.articles.comments.di.qualifiers.RemoteCommentsDataSource
import com.xently.common.di.qualifiers.coroutines.IODispatcher
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
    fun provideCommentsRepository(
        @LocalCommentsDataSource
        local: ICommentsDataSource,
        @RemoteCommentsDataSource
        remote: ICommentsDataSource,
        @IODispatcher
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): ICommentsRepository = CommentsRepository(local, remote, ioDispatcher)
}