package com.xently.news.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ArticleWithMedia(
    @Embedded val a: Article,
    @Relation(parentColumn = "id", entityColumn = "articleId")
    val media: List<Medium> = emptyList()
) {
    val article: Article
        get() = a.copy(media = media)
}