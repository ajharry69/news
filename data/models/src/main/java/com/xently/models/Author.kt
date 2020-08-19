package com.xently.models

import android.os.Parcel
import android.os.Parcelable

data class Author(
    val firstName: String? = null,
    val lastName: String? = null,
    val photoUrl: String? = null
) : Parcelable {
    val name: String?
        get() = "${firstName ?: ""} ${lastName ?: ""}".run {
            if (isBlank()) null else this
        }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(firstName)
            writeString(lastName)
            writeString(photoUrl)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Author> {
        override fun createFromParcel(parcel: Parcel): Author = Author(parcel)

        override fun newArray(size: Int): Array<Author?> = arrayOfNulls(size)
    }
}