package com.xently.articles.comments.data.source

import com.xently.common.data.TaskResult
import com.xently.data.source.local.daos.CommentsDAO
import com.xently.models.Comment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentsLocalDataSource @Inject constructor(private val dao: CommentsDAO) :
    ICommentsDataSource {
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