package com.xently.data.source.remote.services

import com.xently.common.data.models.PagedData
import com.xently.common.data.models.PagedData.Companion.DEFAULT_PAGE_SIZE
import com.xently.models.Article
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleService {
    @GET("articles.json")
    suspend fun getArticles(): Response<List<Article>>

    @GET("articles.json")
    suspend fun getArticles(
        @Query("page") page: Int,
        @Query("size") size: Int = DEFAULT_PAGE_SIZE,
    ): Response<PagedData<Article>>

    @GET("articles/{id}.json")
    suspend fun getArticle(@Path("id") id: Long): Response<Article>

    @PATCH("articles/{id}/flag.json")
    suspend fun flagArticle(@Path("id") id: Long): Response<Article>
}