package com.xently.news.data.source.local

import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Error
import com.xently.common.data.TaskResult.Success
import com.xently.news.data.model.Article
import com.xently.news.data.source.IArticleDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticleLocalDataSource @Inject constructor(private val dao: ArticleDAO) : IArticleDataSource {
    override suspend fun saveArticles(vararg articles: Article): TaskResult<List<Article>> {
        val savedArticles = dao.saveArticles(*articles)
        return if (savedArticles.isNotEmpty()) Success(articles.toList()) else {
            Error(Exception("Error saving ${articles.size - savedArticles.size} articles"))
        }
    }

    override suspend fun addBookMark(articleId: Long, bookmark: Boolean): TaskResult<Boolean> {
        val isBookmarked = dao.addBookMark(articleId, bookmark) > 0
        return if (isBookmarked) Success(isBookmarked && bookmark) else Error(Exception("Error adding bookmark"))
    }

    override suspend fun getArticles(searchQuery: String?): TaskResult<List<Article>> {
        val articles =
            if (searchQuery.isNullOrBlank()) dao.getArticles() else dao.getArticles(searchQuery)
        return Success(articles.map { it.article })
    }

    override suspend fun getArticle(id: Long): TaskResult<Article> {
        val article = dao.getArticle(id)
        return if (article != null) Success(article.article)
        else Error(Exception("Article with ID $id not found"))
    }

    override suspend fun getObservableArticles(
        searchQuery: String?,
        source: Source
    ): Flow<List<Article>> {
        val articles = if (searchQuery.isNullOrBlank()) dao.getObservableArticles()
        else dao.getObservableArticles(searchQuery)
        return articles.map { articleList -> articleList.map { it.article } }
    }

    override suspend fun getObservableArticle(id: Long, source: Source): Flow<Article> =
        dao.getObservableArticle(id).map { it.article }
}