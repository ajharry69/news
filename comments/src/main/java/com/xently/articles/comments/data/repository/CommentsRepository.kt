package com.xently.articles.comments.data.repository

import com.xently.articles.comments.data.source.ICommentsDataSource
import com.xently.articles.comments.di.qualifiers.LocalCommentsDataSource
import com.xently.articles.comments.di.qualifiers.RemoteCommentsDataSource
import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.data
import com.xently.common.data.listData
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.common.utils.wrapEspressoIdlingResource
import com.xently.models.Comment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentsRepository @Inject constructor(
    @LocalCommentsDataSource
    private val local: ICommentsDataSource,
    @RemoteCommentsDataSource
    private val remote: ICommentsDataSource,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ICommentsRepository {
    override suspend fun addComments(vararg comments: Comment) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            remote.addComments(*comments).run {
                local.addComments(*listData.toTypedArray())
            }
        }
    }

    override suspend fun getComments(articleId: Long, searchQuery: String?, refresh: Boolean) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                remote.getComments(articleId, searchQuery, refresh).run {
                    if (refresh) local.deleteComments(articleId)
                    local.addComments(*listData.toTypedArray())
                    local.getComments(articleId, searchQuery, refresh)
                }
            }
        }

    override suspend fun getComments(
        articleId: Long,
        page: Int,
        size: Int,
        searchQuery: String?,
        refresh: Boolean,
    ) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            remote.getComments(articleId, page, size, searchQuery, refresh).apply {
                if (refresh) local.deleteComments(articleId)
                data?.let {
                    local.addComments(*it.results.toTypedArray())
                }
            }
        }
    }

    override suspend fun getObservableComments(
        articleId: Long,
        searchQuery: String?,
        source: Source,
    ) = wrapEspressoIdlingResource {
        when (source) {
            REMOTE -> remote.getObservableComments(articleId, searchQuery, source)
            LOCAL -> local.getObservableComments(articleId, searchQuery, source)
        }
    }

    override suspend fun deleteComments(articleId: Long, vararg ids: Long) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                local.deleteComments(articleId, *ids).run {
                    remote.deleteComments(articleId, *ids)
                }
            }
        }

    override suspend fun deleteComments(articleId: Long) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            local.deleteComments(articleId).run {
                remote.deleteComments(articleId)
            }
        }
    }
}