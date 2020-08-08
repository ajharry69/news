package com.xently.news.data.source.remote

import com.xently.news.data.source.IArticleDataSource
import javax.inject.Inject

class ArticleRemoteDataSource @Inject constructor(private val service: ArticleService) :
    IArticleDataSource {
}