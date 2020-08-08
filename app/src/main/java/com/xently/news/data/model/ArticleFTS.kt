package com.xently.news.data.model

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "articles_fts")
@Fts4(contentEntity = Article::class)
data class ArticleFTS(@PrimaryKey val rowid: Long, val headline: String, val content: String)