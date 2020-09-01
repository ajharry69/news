package com.xently.news.data

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult.Success
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
import javax.inject.Singleton

@Singleton
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
            val pageConfig = state.config
            val initialPageMultiplier = pageConfig.initialLoadSize / pageConfig.pageSize
            val (page, limit) = when (loadType) {
                REFRESH -> Pair(null, pageConfig.initialLoadSize)
                PREPEND -> return Success(true)
                APPEND -> {
                    val json = preferences.getString(SHARED_PREFERENCE_KEY_ARTICLE_PAGE_DATA, null)
                    val data = PagedData.fromJson<Article>(json)
                        .copy(initialPageMultiplier = initialPageMultiplier)
                    if (data.isDataLoadFinished) return Success(true)
                    Pair(data.nextPage, pageConfig.pageSize)
                }
            }
            val response = service.getArticles(page ?: 1, limit)
            val isRefresh = loadType == REFRESH
            val data = response.body()
                ?.copy(isRefresh = isRefresh, initialPageMultiplier = initialPageMultiplier)
                ?: throw response.error().exception
            with(database) {
                withTransaction {
                    articlesDAO.run {
                        preferences.edit {
                            if (isRefresh) {
                                remove(SHARED_PREFERENCE_KEY_ARTICLE_PAGE_DATA)
                                deleteArticles()
                            }
                            putString(SHARED_PREFERENCE_KEY_ARTICLE_PAGE_DATA, data.toString())
                        }
                        saveArticles(*data.results.toTypedArray())
                    }
                    mediaDAO.saveMedia(*data.results.media().toTypedArray())
                }
            }
            Success(data.isDataLoadFinished)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}