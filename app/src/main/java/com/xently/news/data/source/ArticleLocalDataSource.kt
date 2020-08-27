package com.xently.news.data.source

import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Error
import com.xently.common.data.TaskResult.Success
import com.xently.data.source.local.daos.ArticleDAO
import com.xently.data.source.local.daos.MediaDAO
import com.xently.models.Article
import com.xently.models.media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticleLocalDataSource @Inject constructor(
    private val dao: ArticleDAO,
    private val mediaDao: MediaDAO,
) : IArticleDataSource {
    override suspend fun saveArticles(vararg articles: Article): TaskResult<List<Article>> {
        val savedArticles = dao.saveArticles(*articles)
        return if (savedArticles.isNotEmpty()) {
            // medium has foreign key assoc with article hence MUST be saved after saving articles
            mediaDao.saveMedia(*articles.media().toTypedArray())
            Success(articles.toList())
        } else Error("Error saving ${articles.size - savedArticles.size} articles")
    }

    override suspend fun addBookMark(articleId: Long, bookmark: Boolean): TaskResult<Boolean> {
        val isBookmarked = dao.addBookMark(articleId, bookmark) > 0
        return if (isBookmarked) Success(isBookmarked && bookmark) else Error("Error adding bookmark")
    }

    override suspend fun getArticles(
        searchQuery: String?,
        refresh: Boolean,
    ): TaskResult<List<Article>> {
        val articles =
            if (searchQuery.isNullOrBlank()) dao.getArticles() else dao.getArticles(searchQuery)
        return Success(articles.map { it.article })
    }

    override fun getArticlePagingSource(query: String?, source: Source) =
        if (query.isNullOrBlank()) dao.getPaginatedArticles() else dao.getPaginatedArticles(query)

    override suspend fun getArticle(id: Long): TaskResult<Article> {
        val article = dao.getArticle(id)
        return if (article != null) Success(article.article) else Error("Article with ID $id not found")
    }

    override suspend fun flagArticle(id: Long) = getArticle(id)

    override suspend fun deleteArticles(): TaskResult<Unit> {
        dao.deleteArticles()
        return Success(Unit)
    }

    override suspend fun getObservableArticles(
        searchQuery: String?,
        source: Source,
    ): Flow<List<Article>> {
        val articles = if (searchQuery.isNullOrBlank()) dao.getObservableArticles()
        else dao.getObservableArticles(searchQuery)
        return articles.map { articleList -> articleList.map { it.article } }
    }

    override suspend fun getObservableArticle(id: Long, source: Source): Flow<Article> =
        dao.getObservableArticle(id).map { it.article }
}