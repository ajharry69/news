package com.xently.data.source.local.daos

import androidx.room.*
import com.xently.models.Article
import com.xently.models.ArticleWithMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticles(vararg articles: Article): Array<Long>

    @Query("UPDATE articles SET bookmarked = :bookmark WHERE id = :articleId")
    suspend fun addBookMark(articleId: Long, bookmark: Boolean): Int

    @Transaction
    @Query("SELECT * FROM articles ORDER BY publicationDate DESC")
    fun getArticles(): List<ArticleWithMedia>

    @Transaction
    @Query("SELECT a.* FROM articles_fts INNER JOIN articles AS a ON (articles_fts.rowid = a.id) WHERE articles_fts MATCH :query ORDER BY publicationDate DESC")
    fun getArticles(query: String): List<ArticleWithMedia>

    @Transaction
    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticle(id: Long): ArticleWithMedia?

    @Transaction
    @Query("SELECT * FROM articles ORDER BY publicationDate DESC")
    fun getObservableArticles(): Flow<List<ArticleWithMedia>>

    @Transaction
    @Query("SELECT a.* FROM articles_fts INNER JOIN articles AS a ON (articles_fts.rowid = a.id) WHERE articles_fts MATCH :query ORDER BY publicationDate DESC")
    fun getObservableArticles(query: String): Flow<List<ArticleWithMedia>>

    @Transaction
    @Query("SELECT * FROM articles WHERE id = :id")
    fun getObservableArticle(id: Long): Flow<ArticleWithMedia>
}