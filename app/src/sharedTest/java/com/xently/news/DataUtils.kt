package com.xently.news

import com.xently.news.data.model.Article
import com.xently.news.data.model.Author
import com.xently.news.data.model.Media

val ARTICLE = Article(
    1,
    "Article 1",
    "Contents of Article 1",
    author = Author("John", "Doe"),
    media = listOf(Media(1, "", articleId = 1)),
    tags = listOf("technology", "android", "google", "kotlin"),
    url = "https://domain.xyz/1/"
)

fun createArticles(number: Int): List<Article> {
    if (number < 1) return emptyList()
    return (0 until number).map {
        val id = it + 2
        Article(
            id.toLong(), "Article $id headline!", "Article $id contents",
            author = Author("AuthorFn$id", "AuthorLn$id"),
            tags = (0 until it).map { it1 -> "tag$it1" },
            url = "https://domain.xyz/$id/"
        )
    }
}