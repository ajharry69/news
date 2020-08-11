package com.xently.common.data

sealed class TaskResult<out R> {
    data class Success<out T>(val data: T) : TaskResult<T>() {
        override fun toString(): String = super.toString()
    }

    data class Error(val error: Throwable) : TaskResult<Nothing>() {
        constructor(error: String) : this(Exception(error))
    }

    companion object Loading : TaskResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> super.toString()
            is Error -> error.message!!
            is Loading -> "Loading..."
        }
    }
}

inline val TaskResult<*>.isLoading
    get() = this is TaskResult.Loading

inline val TaskResult<*>.isSuccessful
    get() = this is TaskResult.Success && data != null

inline val <T> TaskResult<T>.data: T?
    get() = if (isSuccessful) (this as TaskResult.Success).data else null

inline val <T> TaskResult<T>.dataOrFail: T
    get() = try {
        this.data!!
    } catch (ex: Exception) {
        throw Exception("Invalid ${TaskResult::class.java.simpleName} type! ${ex.message}")
    }

inline val <T> TaskResult<List<T>>.listData: List<T>
    get() = data ?: emptyList()

inline val TaskResult<*>.errorMessage
    get() = if (this is TaskResult.Error) error.localizedMessage ?: error.message else null

inline val TaskResult<*>.isError
    get() = this is TaskResult.Error