package com.xently.news.data.model

import android.os.Parcel
import android.os.Parcelable
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
    val thumbnailUrl: String? = null,
    val articleId: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeLong(id)
            writeString(url)
            writeString(thumbnailUrl)
            writeLong(articleId)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media = Media(parcel)

        override fun newArray(size: Int): Array<Media?> = arrayOfNulls(size)
    }
}