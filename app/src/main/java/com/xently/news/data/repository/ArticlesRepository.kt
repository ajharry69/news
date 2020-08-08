package com.xently.news.data.repository

import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import com.xently.news.data.source.IArticleDataSource
import com.xently.news.di.qualifiers.LocalArticlesDataSource
import com.xently.news.di.qualifiers.RemoteArticlesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    @LocalArticlesDataSource
    local: IArticleDataSource,
    @RemoteArticlesDataSource
    remote: IArticleDataSource,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IArticlesRepository {
    override suspend fun saveArticles(vararg articles: Article): TaskResult<List<Article>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArticles(searchQuery: String?): TaskResult<List<Article>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArticle(id: Long): TaskResult<Article> {
        TODO("Not yet implemented")
    }

    override suspend fun getObservableArticles(
        searchQuery: String?,
        source: Source
    ): Flow<List<Article>> {
        TODO("Not yet implemented")
    }

    override suspend fun getObservableArticle(id: Long, source: Source): Flow<Article> {
        TODO("Not yet implemented")
    }
}