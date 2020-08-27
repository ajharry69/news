package com.xently.news.data

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.xently.common.data.models.PagedData
import com.xently.common.data.models.error
import com.xently.common.di.qualifiers.UnencryptedSharedPreference
import com.xently.data.source.local.NewsDatabase
import com.xently.data.source.remote.services.ArticleService
import com.xently.models.Article
import com.xently.models.ArticleWithMedia
import com.xently.models.media
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ArticleMediator @Inject constructor(
    private val service: ArticleService,
    private val database: NewsDatabase,
    @UnencryptedSharedPreference
    private val preferences: SharedPreferences,
) : RemoteMediator<Int, ArticleWithMedia>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleWithMedia>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                REFRESH -> null
                PREPEND -> return MediatorResult.Success(true)
                APPEND -> {
                    val pageData = PagedData.fromJson<Article>(preferences.getString(
                        SHARED_PREFERENCE_KEY_ARTICLE_PAGE_DATA,
                        null))
                    if (pageData.isDataLoadFinished) return MediatorResult.Success(
                        true)
                    pageData.nextPage
                }
            }

            val response = service.getArticles(page ?: 1, state.config.pageSize + 5)
            val data = response.body() ?: throw response.error().exception
            database.withTransaction {
                database.articlesDAO.run {
                    if (loadType == REFRESH) {
                        preferences.edit {
                            putString(SHARED_PREFERENCE_KEY_ARTICLE_PAGE_DATA, data.toString())
                        }
                        deleteArticles()
                    }
                    saveArticles(*data.results.toTypedArray())
                }
                database.mediaDAO.saveMedia(*data.results.media().toTypedArray())
            }
            MediatorResult.Success(data.isDataLoadFinished)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}