package com.xently.tests.unit

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.seconds

suspend fun <T> LiveData<T>.getValueOrAwaitFlowValue(timeout: Duration = 1.seconds): T? =
    withTimeoutOrNull(timeout) {
        asFlow().first()
    }

suspend fun <T> Flow<T>.getValueOrAwaitFlowValue(timeout: Duration = 1.seconds): T? =
    asLiveData().getValueOrAwaitFlowValue(timeout)