package com.xently.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xently.data.source.local.daos.ArticleDAO
import com.xently.data.source.local.daos.CommentsDAO
import com.xently.data.source.local.daos.MediaDAO
import com.xently.models.Article
import com.xently.models.ArticleFTS
import com.xently.models.Comment
import com.xently.models.Medium

@Database(
    entities = [
        ArticleFTS::class,
        Article::class,
        Medium::class,
        Comment::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters.StringListConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract val articlesDAO: ArticleDAO
    abstract val mediaDAO: MediaDAO
    abstract val commentsDAO: CommentsDAO
}