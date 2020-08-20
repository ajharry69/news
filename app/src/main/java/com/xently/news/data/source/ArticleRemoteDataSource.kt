package com.xently.news.data.source

import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Success
import com.xently.common.data.source.remote.BaseRemoteDataSource
import com.xently.data.source.remote.services.ArticleService
import com.xently.models.Article
import com.xently.models.ftsFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticleRemoteDataSource @Inject constructor(
    private val service: ArticleService,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRemoteDataSource<Article>(ioDispatcher), IArticleDataSource {
    override suspend fun saveArticles(vararg articles: Article) =
        Success(articles.toList()).updateObservables()

    override suspend fun addBookMark(articleId: Long, bookmark: Boolean): TaskResult<Boolean> {
        val article = MOCK_DATABASE.firstOrNull { it.id == articleId }
            ?: return TaskResult.Error("Article with id $articleId not found")
        updateObservables(article.copy(bookmarked = bookmark))
        return Success(bookmark)
    }

    override suspend fun getArticles(searchQuery: String?): TaskResult<List<Article>> {
        val results = sendRequest { service.getArticles() }
        return (if (results is Success) {
            Success(results.data.ftsFilter(searchQuery))
        } else results).updateObservables()
    }

    override suspend fun getArticle(id: Long) =
        sendRequest { service.getArticle(id) }.updateObservable()

    override suspend fun getObservableArticles(searchQuery: String?, source: Source) =
        Transformations.map(observables) {
            it.ftsFilter(searchQuery).toList()
        }.asFlow()

    override suspend fun getObservableArticle(id: Long, source: Source) =
        Transformations.map(observables) { articles ->
            articles.firstOrNull { it.id == id }
        }.asFlow().map { it ?: throw RuntimeException("Article with id $id not found") }
}