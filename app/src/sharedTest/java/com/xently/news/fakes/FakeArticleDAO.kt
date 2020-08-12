package com.xently.news.fakes

import com.xently.news.data.model.Article
import com.xently.news.data.model.ArticleWithMedia
import com.xently.news.data.source.local.ArticleDAO
import kotlinx.coroutines.flow.Flow

class FakeArticleDAO : ArticleDAO {
    override suspend fun saveArticles(vararg articles: Article): Array<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun addBookMark(articleId: Long, bookmark: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun getArticles(): List<ArticleWithMedia> {
        TODO("Not yet implemented")
    }

    override fun getArticles(query: String): List<ArticleWithMedia> {
        TODO("Not yet implemented")
    }

    override suspend fun getArticle(id: Long): ArticleWithMedia? {
        TODO("Not yet implemented")
    }

    override fun getObservableArticles(): Flow<List<ArticleWithMedia>> {
        TODO("Not yet implemented")
    }

    override fun getObservableArticles(query: String): Flow<List<ArticleWithMedia>> {
        TODO("Not yet implemented")
    }

    override fun getObservableArticle(id: Long): Flow<ArticleWithMedia> {
        TODO("Not yet implemented")
    }
}