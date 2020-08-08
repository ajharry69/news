package com.xently.common.data.models

data class ServerError(
    val error: String?,
    val detail: String?,
    val code: String? = null,
    val errors: Any? = null,
    val metadata: Any? = null
)