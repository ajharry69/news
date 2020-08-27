package com.xently.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.xently.common.data.models.PagedData
import com.xently.common.utils.Exclude
import com.xently.models.util.ChipData
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.ROOT

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
    val media: List<Medium> = emptyList(),
    var tags: Set<String> = emptySet(),
    var url: String? = null,
    @Exclude
    var bookmarked: Boolean = false,
    var mediaThumbnail: String? = null,
    var commentsCount: Int = 0,
    var flagCount: Int = 0,
    var flaggedByMe: Boolean = false,
) : Parcelable {

    val mediaUris: List<String>
        get() = media.map { it.url }

    val chipDataList: List<ChipData>
        get() = tags.map { ChipData(it) }

    val subHeadline: String
        @Ignore
        get() = "${author.name ?: ""} - $publicationDate".trimStart()

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable<Author>(Author::class.java.classLoader) ?: Author(),
        parcel.createTypedArrayList(Medium) ?: emptyList()
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

fun Collection<Article>.media() = flatMap { listOf(*it.media.toTypedArray()) }

fun Array<out Article>.media() = flatMap { listOf(*it.media.toTypedArray()) }

fun Collection<Article>.ftsFilter(query: String?): List<Article> {
    return if (query.isNullOrBlank()) toList() else {
        filter {
            it.content.toLowerCase(ROOT).contains(query.toLowerCase(ROOT))
                    || it.headline.toLowerCase(ROOT).contains(query.toLowerCase(ROOT))
        }
    }
}

fun PagedData<Article>.ftsFilter(query: String?) = copy(results = results.ftsFilter(query))