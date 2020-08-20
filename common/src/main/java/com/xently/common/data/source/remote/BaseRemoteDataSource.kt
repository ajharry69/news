package com.xently.common.data.source.remote

import com.xently.common.data.TaskResult
import com.xently.common.data.models.ServerError
import com.xently.common.data.source.BaseDataSource
import com.xently.common.utils.JSON_CONVERTER
import com.xently.common.utils.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class BaseRemoteDataSource<M>(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    BaseDataSource<M>() {
    @Suppress("UNCHECKED_CAST", "BlockingMethodInNonBlockingContext")
    protected suspend fun <T> sendRequest(request: suspend () -> Response<T>): TaskResult<T> {
        return try {
            val response = request.invoke() // Initiate actual network request call
            val (statusCode, body, errorBody) = Triple(
                response.code(),
                response.body(),
                response.errorBody()
            )
            if (response.isSuccessful) {
                val alt = Any() as T
                TaskResult.Success(if (statusCode == 204) alt else body ?: alt)
            } else {
                withContext<TaskResult<T>>(ioDispatcher) {
                    val error = try {
                        JSON_CONVERTER.fromJson(
                            errorBody?.string(),
                            ServerError::class.java
                        )
                    } catch (ex: IllegalStateException) {
                        Log.show(TAG, ex.message, ex, Log.Type.ERROR)
                        ServerError("An error occurred", null)
                    }
                    TaskResult.Error(Exception(error.error))
                }
            }
        } catch (ex: Exception) {
            Log.show(TAG, ex.message, ex, Log.Type.ERROR)
            TaskResult.Error(ex)
        }
    }
}