package com.xently.articles.comments.data.source

import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.TaskResult
import com.xently.models.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsDataSource {
    suspend fun addComments(vararg comments: Comment): TaskResult<List<Comment>>

    suspend fun getComments(articleId: Long, searchQuery: String? = null): TaskResult<List<Comment>>

    suspend fun getObservableComments(
        articleId: Long,
        searchQuery: String? = null,
        source: Source = LOCAL,
    ): Flow<List<Comment>>

    suspend fun deleteComment(comment: Comment): TaskResult<Unit>
}