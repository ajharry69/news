package com.xently.articles.comments.data.source

import com.xently.common.data.TaskResult
import com.xently.common.data.source.remote.BaseRemoteDataSource
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.data.source.remote.services.CommentService
import com.xently.models.Comment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentsRemoteDataSource @Inject constructor(
    private val service: CommentService,
    @IODispatcher
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): BaseRemoteDataSource(ioDispatcher), ICommentsDataSource {
    override suspend fun addComments(vararg comments: Comment): TaskResult<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(articleId: Long): TaskResult<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun getObservableComments(articleId: Long): Flow<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteComment(comment: Comment): TaskResult<Unit> {
        TODO("Not yet implemented")
    }
}