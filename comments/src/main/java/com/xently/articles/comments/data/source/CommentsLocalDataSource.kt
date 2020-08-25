package com.xently.articles.comments.data.source

import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Error
import com.xently.common.data.TaskResult.Success
import com.xently.common.data.models.PagedData
import com.xently.data.source.local.daos.CommentsDAO
import com.xently.models.Comment
import javax.inject.Inject

class CommentsLocalDataSource @Inject constructor(private val dao: CommentsDAO) :
    ICommentsDataSource {
    override suspend fun addComments(vararg comments: Comment): TaskResult<List<Comment>> {
        val savedComments = dao.addComments(*comments)
        return if (savedComments.isNotEmpty()) Success(comments.toList()) else {
            Error("Error saving ${comments.size - savedComments.size} comments")
        }
    }

    override suspend fun getComments(
        articleId: Long,
        searchQuery: String?,
        refresh: Boolean,
    ): TaskResult<List<Comment>> {
        val comments = if (searchQuery.isNullOrBlank()) dao.getComments(articleId)
        else dao.getComments(articleId, "%$searchQuery%")
        return Success(comments)
    }

    override suspend fun getComments(
        articleId: Long,
        page: Int,
        size: Int,
        searchQuery: String?,
        refresh: Boolean,
    ): TaskResult<PagedData<Comment>> {
        val comments = if (searchQuery.isNullOrBlank()) dao.getComments(articleId)
        else dao.getComments(articleId, searchQuery)
        return Success(PagedData(results = comments))
    }

    override suspend fun getObservableComments(
        articleId: Long,
        searchQuery: String?,
        source: Source,
    ) = if (searchQuery.isNullOrBlank()) dao.getObservableComments(articleId)
    else dao.getObservableComments(articleId, "%$searchQuery%")

    override suspend fun deleteComments(articleId: Long, vararg ids: Long): TaskResult<Unit> {
        dao.deleteComments(articleId, *ids)
        return Success(Unit)
    }

    override suspend fun deleteComments(articleId: Long): TaskResult<Unit> {
        dao.deleteComments(articleId)
        return Success(Unit)
    }
}