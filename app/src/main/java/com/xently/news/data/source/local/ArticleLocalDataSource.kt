package com.xently.news.data.source.local

import com.xently.news.data.source.IArticleDataSource
import javax.inject.Inject

class ArticleLocalDataSource @Inject constructor(private val dao: ArticleDAO) : IArticleDataSource {
}