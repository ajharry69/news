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
import com.xently.news.data.source.IArticleDataSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import java.util.*

class FakeArticleDataSource(vararg articles: Article) : IArticleDataSource {
    private val observableArticles = MutableLiveData<List<Article>>()

    init {
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
        return Success(ARTICLES_DB.filter(searchQuery)).updateObservableArticles()
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
            it.filter(searchQuery).toList()
        }.asFlow()

    @FlowPreview
    override suspend fun getObservableArticle(id: Long, source: Source) =
        Transformations.map(observableArticles) { articles ->
            articles.firstOrNull { it.id == id }
        }.asFlow().flatMapConcat {
            if (it == null) {
                throw RuntimeException("Article with id $id not found")
            } else flowOf(it)
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

    private fun Collection<Article>.filter(query: String?): List<Article> {
        return if (query.isNullOrBlank()) toList() else {
            filter {
                it.content.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
                        || it.headline.toLowerCase(Locale.ROOT)
                    .contains(query.toLowerCase(Locale.ROOT))
            }
        }
    }

    companion object {
        private val ARTICLES_DB = mutableSetOf<Article>()
    }
}