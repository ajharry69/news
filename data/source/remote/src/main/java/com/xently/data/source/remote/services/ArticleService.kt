package com.xently.data.source.remote.services

import com.xently.common.data.models.PagedData
import com.xently.models.Article
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleService {
    @GET("articles/")
    suspend fun getArticles(): Response<List<Article>>

    @GET("articles/")
    suspend fun getArticles(
        @Query("page") page: Int,
        @Query("size") size: Int = 50,
    ): Response<PagedData<Article>>

    @GET("articles/{id}/")
    suspend fun getArticle(@Path("id") id: Long): Response<Article>

    @PATCH("articles/{id}/flag/")
    suspend fun flagArticle(@Path("id") id: Long): Response<Article>
}