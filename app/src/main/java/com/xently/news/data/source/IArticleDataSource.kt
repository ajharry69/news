package com.xently.news.data.source

import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import kotlinx.coroutines.flow.Flow

interface IArticleDataSource {
    suspend fun saveArticles(vararg articles: Article): TaskResult<List<Article>>

    suspend fun getArticles(searchQuery: String? = null): TaskResult<List<Article>>

    suspend fun getArticle(id: Long): TaskResult<Article>

    suspend fun getObservableArticles(
        searchQuery: String? = null,
        source: Source = Source.LOCAL
    ): Flow<List<Article>>

    suspend fun getObservableArticle(id: Long, source: Source = Source.LOCAL): Flow<Article>
}