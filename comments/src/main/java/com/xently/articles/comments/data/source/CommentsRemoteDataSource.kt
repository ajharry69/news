package com.xently.articles.comments.data.source

import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Success
import com.xently.common.data.data
import com.xently.common.data.models.PagedData
import com.xently.common.data.source.remote.AbstractRemoteDataSource
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.data.source.remote.services.CommentService
import com.xently.models.Comment
import com.xently.models.ftsFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CommentsRemoteDataSource @Inject constructor(
    private val service: CommentService,
    @IODispatcher
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AbstractRemoteDataSource<Comment>(ioDispatcher), ICommentsDataSource {
    override suspend fun addComments(vararg comments: Comment) = Success(comments.mapNotNull {
        sendRequest {
            service.addComment(it.articleId, it)
        }.data
    }).updateObservables()

    override suspend fun getComments(articleId: Long, searchQuery: String?, refresh: Boolean) =
        sendRequest {
            service.getComments(articleId)
        }.updateObservables()

    override suspend fun getComments(
        articleId: Long,
        page: Int,
        size: Int,
        searchQuery: String?,
        refresh: Boolean,
    ): TaskResult<PagedData<Comment>> {
        val result = sendRequest { service.getComments(articleId, page, size) }
        return (if (result is Success) {
            result.copy(data = result.data.ftsFilter(searchQuery))
        } else result).updateObservablesFromPagedData()
    }

    override suspend fun getObservableComments(
        articleId: Long,
        searchQuery: String?,
        source: Source,
    ) = Transformations.map(observables) { it.ftsFilter(searchQuery).toList() }.asFlow()

    override suspend fun deleteComments(articleId: Long, vararg ids: Long) = sendRequest {
        service.deleteComment(articleId, ids.first()).apply {
            deleteAndUpdateObservable(MOCK_DATABASE.firstOrNull { it.id == ids.first() })
        }
    }

    override suspend fun deleteComments(articleId: Long): TaskResult<Unit> {
        cleanUpObservables()
        return Success(Unit)
    }
}