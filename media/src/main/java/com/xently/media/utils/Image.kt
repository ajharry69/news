package com.xently.media.utils

import android.net.Uri

data class Image(val uris: Array<out Uri> = emptyArray()) {
    constructor(
        uris: Array<out String> = emptyArray()
    ) : this(uris.map { Uri.parse(it) }.toTypedArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Image) return false

        if (!uris.contentEquals(other.uris)) return false

        return true
    }

    override fun hashCode(): Int = uris.contentHashCode()
}