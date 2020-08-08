package com.xently.news.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xently.news.data.model.Article
import com.xently.news.data.model.Media

@Database(
    entities = [
        Article::class,
        Media::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NewsDatabase : RoomDatabase() {
    abstract val articlesDAO: ArticleDAO
}