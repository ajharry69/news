package com.xently.news.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.xently.news.data.model.Medium

@Dao
interface MediaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMedia(vararg media: Medium): Array<Long>
}