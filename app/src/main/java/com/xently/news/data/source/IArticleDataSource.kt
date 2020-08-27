package com.xently.news.data.source

import androidx.paging.PagingSource
import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.TaskResult
import com.xently.models.Article
import com.xently.models.ArticleWithMedia
import kotlinx.coroutines.flow.Flow

interface IArticleDataSource {
    /**
     * Saves [articles] and returns those([articles]) saved successfully wrapped in [TaskResult]
     */
    suspend fun saveArticles(vararg articles: Article): TaskResult<List<Article>>

    suspend fun addBookMark(articleId: Long, bookmark: Boolean): TaskResult<Boolean>

    suspend fun getArticles(
        searchQuery: String? = null,
        refresh: Boolean = false,
    ): TaskResult<List<Article>>

    /**
     * @param source only useful in Repository
     */
    fun getArticlePagingSource(
        query: String? = null,
        source: Source = LOCAL,
    ): PagingSource<Int, ArticleWithMedia>

    suspend fun getArticle(id: Long): TaskResult<Article>

    suspend fun flagArticle(id: Long): TaskResult<Article>

    suspend fun deleteArticles(): TaskResult<Unit>

    suspend fun getObservableArticles(
        searchQuery: String? = null,
        source: Source = LOCAL,
    ): Flow<List<Article>>

    suspend fun getObservableArticle(id: Long, source: Source = LOCAL): Flow<Article>
}