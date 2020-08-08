package com.xently.news.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = false)
    var id: Long = -1,
    var headline: String = "",
    var content: String = "",
    var publicationDate: String = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    ).format(Date()),
    @Ignore
    @SerializedName("media_urls")
    val media: List<Media> = emptyList()
)