package com.xently.news.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.data
import com.xently.common.data.listData
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.common.utils.wrapEspressoIdlingResource
import com.xently.models.Article
import com.xently.news.data.ArticleMediator
import com.xently.news.data.source.IArticleDataSource
import com.xently.news.di.qualifiers.LocalArticlesDataSource
import com.xently.news.di.qualifiers.RemoteArticlesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesRepository @Inject constructor(
    @LocalArticlesDataSource
    private val local: IArticleDataSource,
    @RemoteArticlesDataSource
    private val remote: IArticleDataSource,
    private val mediator: ArticleMediator? = null,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IArticlesRepository {
    override suspend fun saveArticles(vararg articles: Article) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            remote.saveArticles(*articles).run {
                local.saveArticles(*listData.toTypedArray())
            }
        }
    }

    override suspend fun addBookMark(articleId: Long, bookmark: Boolean) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                remote.addBookMark(articleId, bookmark).run {
                    local.addBookMark(articleId, bookmark)
                }
            }
        }

    override fun getArticles(size: Int, searchQuery: String?, enablePlaceholders: Boolean) =
        wrapEspressoIdlingResource {
            Pager(config = PagingConfig(size, enablePlaceholders = enablePlaceholders),
                remoteMediator = mediator) {
                getArticlePagingSource(searchQuery, LOCAL)
            }.flow.map { data -> data.map { it.article } }
        }

    override suspend fun getArticles(searchQuery: String?, refresh: Boolean) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                remote.getArticles(searchQuery, refresh).run {
                    if (refresh) local.deleteArticles()
                    local.saveArticles(*listData.toTypedArray())
                    local.getArticles(searchQuery, refresh)
                }
            }
        }

    override fun getArticlePagingSource(query: String?, source: Source) = when (source) {
        REMOTE -> remote.getArticlePagingSource(query, source)
        LOCAL -> local.getArticlePagingSource(query, source)
    }

    override suspend fun getArticle(id: Long) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            remote.getArticle(id).apply {
                data?.also { article ->
                    local.saveArticles(article)
                }
            }
        }
    }

    override suspend fun flagArticle(id: Long) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            remote.flagArticle(id).apply {
                data?.also { article ->
                    local.saveArticles(article)
                }
            }
        }
    }

    override suspend fun deleteArticles() = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            local.deleteArticles().apply {
                remote.deleteArticles()
            }
        }
    }

    override suspend fun getObservableArticles(searchQuery: String?, source: Source) =
        wrapEspressoIdlingResource {
            when (source) {
                REMOTE -> remote.getObservableArticles(searchQuery, source)
                LOCAL -> local.getObservableArticles(searchQuery, source)
            }
        }

    override suspend fun getObservableArticle(id: Long, source: Source) =
        wrapEspressoIdlingResource {
            when (source) {
                REMOTE -> remote.getObservableArticle(id, source)
                LOCAL -> local.getObservableArticle(id, source)
            }
        }
}