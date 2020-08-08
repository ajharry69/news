package com.xently.news.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val headline: String,
    val content: String,
    @Ignore
    val mediaUrls: List<Media> = emptyList()
)