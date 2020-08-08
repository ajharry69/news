package com.xently.news.data.source.remote

import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import com.xently.news.data.source.IArticleDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArticleRemoteDataSource @Inject constructor(
    private val service: ArticleService,
    ioDispatcher: CoroutineDispatcher
) :
    IArticleDataSource {
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