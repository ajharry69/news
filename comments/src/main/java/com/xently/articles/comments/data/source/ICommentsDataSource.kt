package com.xently.articles.comments.data.source

import com.xently.common.data.TaskResult
import com.xently.models.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsDataSource {
    suspend fun addComments(vararg comments: Comment): TaskResult<List<Comment>>

    suspend fun getComments(articleId: Long): TaskResult<List<Comment>>

    suspend fun getObservableComments(articleId: Long): Flow<List<Comment>>

    suspend fun deleteComment(comment: Comment): TaskResult<Unit>
}