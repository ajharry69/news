package com.xently.common.data.models

import com.google.gson.reflect.TypeToken
import com.xently.common.utils.Exclude
import com.xently.common.utils.JSON_CONVERTER

data class PagedData<T>(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    @Exclude(during = Exclude.During.SERIALIZATION)
    val results: List<T> = emptyList(),
) {
    val isDataLoadFinished: Boolean
        get() = next.isNullOrBlank()

    val nextPage: Int
        get() = next?.run {
            Regex(".+(?<page>[Pp][Aa][Gg][Ee]=\\d+).*").find(next)?.destructured?.component1()
                ?.let {
                    Regex("\\d+").find(it)?.value
                }?.toIntOrNull() ?: 1
        } ?: 1

    override fun toString(): String = JSON_CONVERTER.toJson(this)

    companion object {
        const val DEFAULT_PAGE_SIZE = 15
        fun <T> fromJson(json: String?) =
            if (json.isNullOrBlank()) PagedData<T>() else JSON_CONVERTER.fromJson(json,
                object : TypeToken<PagedData<T>>() {}.type)
    }
}