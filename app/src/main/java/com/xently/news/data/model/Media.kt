package com.xently.news.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Article::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            name = "media_article_id_idx",
            value = ["id", "articleId"],
            unique = true
        ),
        Index(
            name = "articles_id_idx",
            value = ["articleId"]
        )
    ]
)
data class Media(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val url: String,
    val articleId: Long
)