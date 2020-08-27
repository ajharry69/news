package com.xently.news.fakes

import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import androidx.paging.PagingSource
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Error
import com.xently.common.data.TaskResult.Success
import com.xently.common.data.source.AbstractDataSource
import com.xently.models.Article
import com.xently.models.ArticleWithMedia
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

    override suspend fun getArticles(
        searchQuery: String?,
        refresh: Boolean,
    ) = Success(MOCK_DATABASE.ftsFilter(searchQuery)).updateObservables()

    override fun getArticlePagingSource(query: String?, source: Source): PagingSource<Int, ArticleWithMedia> {
        TODO("Not yet implemented")
    }

    override suspend fun getArticle(id: Long): TaskResult<Article> {
        val article = MOCK_DATABASE.firstOrNull { it.id == id }
        return if (article == null) Error("Article with id $id not found") else {
            Success(article).updateObservable()
        }
    }

    override suspend fun flagArticle(id: Long): TaskResult<Article> {
        val article = MOCK_DATABASE.firstOrNull { it.id == id } ?: return getArticle(id)
        val flaggedByMe = !article.flaggedByMe
        MOCK_DATABASE.add(
            article.copy(
                flaggedByMe = flaggedByMe,
                flagCount = article.flagCount + if (flaggedByMe) 1 else -1
            )
        )
        return getArticle(id)
    }

    override suspend fun deleteArticles(): TaskResult<Unit> {
//        cleanUpObservables()
        return Success(Unit)
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