package com.xently.news.data.repository

import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.data
import com.xently.common.data.listData
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.common.utils.wrapEspressoIdlingResource
import com.xently.models.Article
import com.xently.news.data.source.IArticleDataSource
import com.xently.news.di.qualifiers.LocalArticlesDataSource
import com.xently.news.di.qualifiers.RemoteArticlesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    @LocalArticlesDataSource
    private val local: IArticleDataSource,
    @RemoteArticlesDataSource
    private val remote: IArticleDataSource,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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

    override suspend fun getArticles(searchQuery: String?) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            remote.getArticles(searchQuery).run {
                local.saveArticles(*listData.toTypedArray())
                local.getArticles(searchQuery)
            }
        }
    }

    override suspend fun getArticle(id: Long) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            remote.getArticle(id).run {
                data?.also { article ->
                    local.saveArticles(article)
                }
                this
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

    override suspend fun getObservableArticle(id: Long, source: Source): Flow<Article> =
        wrapEspressoIdlingResource {
            when (source) {
                REMOTE -> remote.getObservableArticle(id, source)
                LOCAL -> local.getObservableArticle(id, source)
            }
        }
}