package com.xently.news.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Article::class,
            parentColumns = ["id"],
            childColumns = ["articleId"]
        )
    ]
)
data class Media(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val url: String,
    val articleId: Long
)