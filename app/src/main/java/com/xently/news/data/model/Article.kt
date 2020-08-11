package com.xently.news.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.xently.common.utils.Exclude
import com.xently.news.ui.utils.ChipData
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
    var creationTime: String = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SZ",
        Locale.getDefault()
    ).format(Date()),
    var updateTime: String = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SZ",
        Locale.getDefault()
    ).format(Date()),
    @Embedded
    var author: Author = Author(),
    @Ignore
    @SerializedName("media_urls")
    val media: List<Media> = emptyList(),
    @Ignore
    val tags: List<String> = emptyList(),
    var url: String? = null,
    @Exclude
    var bookmarked: Boolean = false
) : Parcelable {

    val chipDataList: List<ChipData>
        get() = tags.map { ChipData(it) }

    val mediaThumbnailUrl: String?
        get() = if (media.isEmpty()) null else media[0].thumbnailUrl

    val subHeadline: String
        @Ignore
        get() = "${author.name ?: ""} $publicationDate".trimStart()

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable<Author>(Author::class.java.classLoader) ?: Author(),
        parcel.createTypedArrayList(Media) ?: emptyList()
    )

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + headline.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + publicationDate.hashCode()
        result = 31 * result + creationTime.hashCode()
        result = 31 * result + updateTime.hashCode()
        result = 31 * result + media.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Article) return false

        if (id != other.id) return false
        if (headline != other.headline) return false
        if (content != other.content) return false
        if (publicationDate != other.publicationDate) return false
        if (publicationDate != other.creationTime) return false
        if (publicationDate != other.updateTime) return false
        if (media != other.media) return false

        return true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeLong(id)
            writeString(headline)
            writeString(content)
            writeString(publicationDate)
            writeString(creationTime)
            writeString(updateTime)
            writeParcelable(author, flags)
            writeTypedList(media)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article = Article(parcel)

        override fun newArray(size: Int): Array<Article?> = arrayOfNulls(size)
    }
}