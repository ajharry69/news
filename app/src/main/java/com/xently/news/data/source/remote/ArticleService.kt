package com.xently.news.data.source.remote

import com.xently.news.data.model.Article
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleService {
    @GET("articles/")
    suspend fun getArticles(): Response<List<Article>>

    @GET("articles/{id}/")
    suspend fun getArticle(@Path("id") id: Long): Response<Article>
}