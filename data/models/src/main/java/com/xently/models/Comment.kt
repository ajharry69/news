package com.xently.models

import android.os.Parcel
import android.os.Parcelable

data class Comment(
    val id: Long = Long.MIN_VALUE,
    val message: String = "",
    val author: Author = Author(),
    val replies: List<Comment> = emptyList(),
    val articleId: Long = Long.MIN_VALUE,
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