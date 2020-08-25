package com.xently.common.data.models

data class PagedData<T>(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T> = emptyList(),
) {
    val isDataLoadFinished: Boolean
        get() = next.isNullOrBlank()

    val nextPage: Int?
        get() = if (next.isNullOrBlank()) {
            null
        } else {
            Regex(".+(?<page>[Pp][Aa][Gg][Ee]=\\d+).*").find(next)?.destructured?.component1()
                ?.let {
                    Regex("\\d+").find(it)?.destructured?.component1()
                }?.toIntOrNull()
        }
}