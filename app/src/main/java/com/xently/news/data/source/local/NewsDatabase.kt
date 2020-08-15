package com.xently.news.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xently.news.data.model.Article
import com.xently.news.data.model.ArticleFTS
import com.xently.news.data.model.Media

@Database(
    entities = [
        ArticleFTS::class,
        Article::class,
        Media::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters.StringListConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract val articlesDAO: ArticleDAO
}