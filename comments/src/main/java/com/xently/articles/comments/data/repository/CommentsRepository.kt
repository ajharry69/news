package com.xently.articles.comments.data.repository

import com.xently.articles.comments.data.source.ICommentsDataSource
import com.xently.articles.comments.di.qualifiers.LocalCommentsDataSource
import com.xently.articles.comments.di.qualifiers.RemoteCommentsDataSource
import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.listData
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.models.Comment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    @LocalCommentsDataSource
    private val local: ICommentsDataSource,
    @RemoteCommentsDataSource
    private val remote: ICommentsDataSource,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ICommentsRepository {
    override suspend fun addComments(vararg comments: Comment) = withContext(ioDispatcher) {
        remote.addComments(*comments).run {
            local.addComments(*listData.toTypedArray())
        }
    }

    override suspend fun getComments(articleId: Long, searchQuery: String?) =
        withContext(ioDispatcher) {
            remote.getComments(articleId, searchQuery).run {
                local.addComments(*listData.toTypedArray())
                local.getComments(articleId, searchQuery)
            }
        }

    override suspend fun getObservableComments(
        articleId: Long,
        searchQuery: String?,
        source: Source
    ) = when (source) {
        REMOTE -> remote.getObservableComments(articleId, searchQuery, source)
        LOCAL -> local.getObservableComments(articleId, searchQuery, source)
    }

    override suspend fun deleteComment(comment: Comment) = withContext(ioDispatcher) {
        local.deleteComment(comment).run {
            remote.deleteComment(comment)
        }
    }
}