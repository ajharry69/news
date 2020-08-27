package com.xently.data.source.remote.services

import com.xently.common.data.models.PagedData
import com.xently.common.data.models.PagedData.Companion.DEFAULT_PAGE_SIZE
import com.xently.models.Comment
import retrofit2.Response
import retrofit2.http.*

interface CommentService {
    @POST("articles/{articleId}/comments.json")
    suspend fun addComment(
        @Path("articleId") articleId: Long,
        @Body comment: Comment,
    ): Response<Comment>

    @GET("articles/{articleId}/comments.json")
    suspend fun getComments(@Path("articleId") articleId: Long): Response<List<Comment>>

    @GET("articles/{articleId}/comments.json")
    suspend fun getComments(
        @Path("articleId") articleId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int = DEFAULT_PAGE_SIZE,
    ): Response<PagedData<Comment>>

    @DELETE("articles/{articleId}/comments/{commentId}.json")
    suspend fun deleteComment(
        @Path("articleId") articleId: Long,
        @Path("articleId") commentId: Long,
    ): Response<Unit>
}