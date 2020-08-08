package com.xently.news.data.repository

import com.xently.news.data.source.IArticleDataSource
import com.xently.news.di.qualifiers.LocalArticlesDataSource
import com.xently.news.di.qualifiers.RemoteArticlesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    @LocalArticlesDataSource
    local: IArticleDataSource,
    @RemoteArticlesDataSource
    remote: IArticleDataSource,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IArticlesRepository {
}