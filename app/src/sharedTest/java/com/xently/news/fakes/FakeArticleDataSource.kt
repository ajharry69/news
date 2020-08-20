package com.xently.news.fakes

import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Error
import com.xently.common.data.TaskResult.Success
import com.xently.common.data.source.AbstractDataSource
import com.xently.models.Article
import com.xently.models.ftsFilter
import com.xently.news.data.source.IArticleDataSource
import kotlinx.coroutines.flow.map

class FakeArticleDataSource(vararg articles: Article) : AbstractDataSource<Article>(*articles),
    IArticleDataSource {
    override suspend fun saveArticles(vararg articles: Article) =
        Success(articles.toList()).updateObservables()

    override suspend fun addBookMark(articleId: Long, bookmark: Boolean): TaskResult<Boolean> {
        val article = MOCK_DATABASE.firstOrNull { it.id == articleId }
            ?: return Error("Article with id $articleId not found")
        updateObservables(article.copy(bookmarked = bookmark))
        return Success(bookmark)
    }

    override suspend fun getArticles(searchQuery: String?): TaskResult<List<Article>> {
        return Success(MOCK_DATABASE.ftsFilter(searchQuery)).updateObservables()
    }

    override suspend fun getArticle(id: Long): TaskResult<Article> {
        val article = MOCK_DATABASE.firstOrNull { it.id == id }
        return if (article == null) Error("Article with id $id not found") else {
            Success(article).updateObservable()
        }
    }

    override suspend fun getObservableArticles(searchQuery: String?, source: Source) =
        Transformations.map(observables) {
            it.ftsFilter(searchQuery).toList()
        }.asFlow()

    override suspend fun getObservableArticle(id: Long, source: Source) =
        Transformations.map(observables) { articles ->
            articles.firstOrNull { it.id == id }
        }.asFlow().map { it ?: throw RuntimeException("Article with id $id not found") }
}