package com.xently.data.source.local.daos

import androidx.room.Dao
import androidx.room.Delete
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

    @Delete
    suspend fun deleteComment(comment: Comment): Int
}