package com.xently.common.data.models

import com.xently.common.utils.JSON_CONVERTER
import com.xently.common.utils.Log
import retrofit2.Response

data class HttpException(
    val error: String?,
    val detail: String?,
    val code: String? = null,
    val errors: Any? = null,
    val metadata: Any? = null,
    @Transient
    val response: Response<*> = Response.success(null),
) : RuntimeException(error)

fun Response<*>.error(): HttpException = try {
    val exception = JSON_CONVERTER.fromJson(
        errorBody()?.string(),
        HttpException::class.java
    ).copy(response = this)
    if (exception.error.isNullOrBlank()) exception.copy(error = message()) else exception
} catch (ex: IllegalStateException) {
    Log.show("HttpError", ex.message, ex, Log.Type.ERROR)
    HttpException(message(), null, response = this)
}