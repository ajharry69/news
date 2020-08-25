package com.xently.data.source.remote.services

import com.xently.common.data.models.PagedData
import com.xently.models.Comment
import retrofit2.Response
import retrofit2.http.*

interface CommentService {
    @POST("articles/{articleId}/comments/")
    suspend fun addComment(
        @Path("articleId") articleId: Long,
        @Body comment: Comment,
    ): Response<Comment>

    @GET("articles/{articleId}/comments/")
    suspend fun getComments(@Path("articleId") articleId: Long): Response<List<Comment>>


    @GET("articles/{articleId}/comments/")
    suspend fun getComments(
        @Path("articleId") articleId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int = 50,
    ): Response<PagedData<Comment>>

    @DELETE("articles/{articleId}/comments/{commentId}/")
    suspend fun deleteComment(
        @Path("articleId") articleId: Long,
        @Path("articleId") commentId: Long,
    ): Response<Unit>
}