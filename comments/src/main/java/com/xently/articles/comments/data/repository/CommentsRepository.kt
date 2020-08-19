package com.xently.articles.comments.data.repository

import com.xently.articles.comments.data.source.ICommentsDataSource
import com.xently.articles.comments.di.qualifiers.LocalCommentsDataSource
import com.xently.articles.comments.di.qualifiers.RemoteCommentsDataSource
import com.xently.common.data.TaskResult
import com.xently.common.di.qualifiers.coroutines.IODispatcher
import com.xently.models.Comment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    @LocalCommentsDataSource
    private val local: ICommentsDataSource,
    @RemoteCommentsDataSource
    private val remote: ICommentsDataSource,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ICommentsRepository {
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