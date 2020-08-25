package com.xently.articles.comments.data.source

import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.TaskResult
import com.xently.common.data.models.PagedData
import com.xently.models.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsDataSource {
    suspend fun addComments(vararg comments: Comment): TaskResult<List<Comment>>

    suspend fun getComments(articleId: Long, searchQuery: String? = null, refresh: Boolean = false): TaskResult<List<Comment>>

    suspend fun getComments(
        articleId: Long,
        page: Int,
        size: Int = 50,
        searchQuery: String? = null,
        refresh: Boolean = false,
    ): TaskResult<PagedData<Comment>>

    suspend fun getObservableComments(
        articleId: Long,
        searchQuery: String? = null,
        source: Source = LOCAL,
    ): Flow<List<Comment>>

    suspend fun deleteComments(articleId: Long, vararg ids: Long): TaskResult<Unit>

    suspend fun deleteComments(articleId: Long): TaskResult<Unit>
}