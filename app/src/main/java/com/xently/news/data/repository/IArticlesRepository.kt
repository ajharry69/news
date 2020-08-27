package com.xently.news.data.repository

import androidx.paging.PagingData
import com.xently.common.data.models.PagedData.Companion.DEFAULT_PAGE_SIZE
import com.xently.models.Article
import com.xently.news.data.source.IArticleDataSource
import kotlinx.coroutines.flow.Flow

interface IArticlesRepository : IArticleDataSource {
    fun getArticles(
        size: Int = DEFAULT_PAGE_SIZE,
        searchQuery: String? = null,
        enablePlaceholders: Boolean = false,
    ): Flow<PagingData<Article>>
}