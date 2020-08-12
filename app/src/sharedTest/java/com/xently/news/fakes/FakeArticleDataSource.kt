package com.xently.news.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Error
import com.xently.common.data.TaskResult.Success
import com.xently.common.data.listData
import com.xently.news.data.model.Article
import com.xently.news.data.model.ftsFilter
import com.xently.news.data.source.IArticleDataSource
import kotlinx.coroutines.flow.map

class FakeArticleDataSource(vararg articles: Article) : IArticleDataSource {
    private val observableArticles = MutableLiveData<List<Article>>()

    init {
        ARTICLES_DB.clear()
        updateObservableArticles(*articles)
    }

    override suspend fun saveArticles(vararg articles: Article) =
        Success(articles.toList()).updateObservableArticles()

    override suspend fun addBookMark(articleId: Long, bookmark: Boolean): TaskResult<Boolean> {
        val article = ARTICLES_DB.firstOrNull { it.id == articleId }
            ?: return Error("Article with id $articleId not found")
        updateObservableArticles(article.copy(bookmarked = bookmark))
        return Success(bookmark)
    }

    override suspend fun getArticles(searchQuery: String?): TaskResult<List<Article>> {
        return Success(ARTICLES_DB.ftsFilter(searchQuery)).updateObservableArticles()
    }

    override suspend fun getArticle(id: Long): TaskResult<Article> {
        val article = ARTICLES_DB.firstOrNull { it.id == id }
        return if (article == null) Error("Article with id $id not found") else {
            updateObservableArticles(article)
            Success(article)
        }
    }

    override suspend fun getObservableArticles(searchQuery: String?, source: Source) =
        Transformations.map(observableArticles) {
            it.ftsFilter(searchQuery).toList()
        }.asFlow()

    override suspend fun getObservableArticle(id: Long, source: Source) =
        Transformations.map(observableArticles) { articles ->
            articles.firstOrNull { it.id == id }
        }.asFlow().map {
            it ?: throw RuntimeException("Article with id $id not found")
        }

    private fun <T : TaskResult<List<Article>>> T.updateObservableArticles() = this.apply {
        listData.updateObservableArticles()
    }

    private fun Collection<Article>.updateObservableArticles() {
        updateObservableArticles(*toTypedArray())
    }

    private fun updateObservableArticles(vararg articles: Article) {
        val articleList = articles.toList()
        ARTICLES_DB.addAll(articleList)
        observableArticles.postValue(articleList)
    }

    companion object {
        private val ARTICLES_DB = mutableSetOf<Article>()
    }
}