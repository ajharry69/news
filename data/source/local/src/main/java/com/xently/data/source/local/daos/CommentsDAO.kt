package com.xently.data.source.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.xently.models.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentsDAO {
    @Insert(onConflict = REPLACE)
    suspend fun addComments(vararg comments: Comment): Array<Long>

    @Query("SELECT * FROM comments WHERE articleId = :articleId")
    fun getComments(articleId: Long): List<Comment>

    @Query("SELECT * FROM comments WHERE articleId = :articleId")
    fun getObservableComments(articleId: Long): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE articleId = :articleId AND message LIKE :query")
    fun getComments(articleId: Long, query: String): List<Comment>

    @Query("SELECT * FROM comments WHERE articleId = :articleId AND message LIKE :query")
    fun getObservableComments(articleId: Long, query: String): Flow<List<Comment>>

    @Query("DELETE FROM comments WHERE articleId = :articleId AND id IN (:commentIds)")
    suspend fun deleteComments(articleId: Long, vararg commentIds: Long): Int

    @Query("DELETE FROM comments WHERE articleId = :articleId")
    suspend fun deleteComments(articleId: Long): Int
}