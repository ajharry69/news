package com.xently.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import java.util.*

@Entity(
    tableName = "comments",
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
            name = "comments_id_article_id_idx",
            value = ["id", "articleId"],
            unique = true
        ),
        Index(
            name = "comments_article_id_idx",
            value = ["articleId"]
        )
    ],
)
data class Comment(
    @PrimaryKey(autoGenerate = false)
    var id: Long = Long.MIN_VALUE,
    var message: String = "",
    @Embedded
    var author: Author = Author(),
    @Ignore
    val replies: List<Comment> = emptyList(),
    var articleId: Long = Long.MIN_VALUE,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readParcelable(Author::class.java.classLoader)!!,
        parcel.createTypedArrayList(CREATOR)!!,
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeLong(id)
            writeString(message)
            writeParcelable(author, flags)
            writeTypedList(replies)
            writeLong(articleId)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment = Comment(parcel)

        override fun newArray(size: Int): Array<Comment?> = arrayOfNulls(size)
    }
}

fun Collection<Comment>.ftsFilter(query: String?): List<Comment> {
    return if (query.isNullOrBlank()) toList() else {
        filter {
            it.message.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
        }
    }
}