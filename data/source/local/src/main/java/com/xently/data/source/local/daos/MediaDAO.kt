package com.xently.data.source.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.xently.models.Medium

@Dao
interface MediaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMedia(vararg media: Medium): Array<Long>
}