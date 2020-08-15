package com.xently.common.utils

import android.util.Log
import com.xently.common.BuildConfig
import com.xently.common.utils.Log.Type.*
import java.util.*

object Log {
    enum class Type {
        ASSERT,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        VERBOSE
    }

    /**
     * Shows logs only when build type is debug
     * @param tag: Log TAG
     * @param message: Log message
     * @param throwable: Exception to accompany the log
     * @see BuildConfig.BUILD_TYPE
     */
    @JvmOverloads
    @JvmStatic
    fun show(
        tag: String,
        message: Any?,
        throwable: Throwable? = null,
        type: Type = DEBUG,
        logRelease: Boolean = false
    ) {
        if ((!isReleaseBuild() || logRelease) && message != null) {
            when (type) {
                DEBUG -> {
                    if (throwable == null) {
                        Log.d(tag, "$message")
                        return
                    }
                    Log.d(tag, "$message", throwable)
                }
                INFO -> {
                    if (throwable == null) {
                        Log.i(tag, "$message")
                        return
                    }
                    Log.i(tag, "$message", throwable)
                }
                WARNING -> {
                    if (throwable == null) {
                        Log.w(tag, "$message")
                        return
                    }
                    Log.w(tag, "$message", throwable)
                }
                ERROR -> {
                    if (throwable == null) {
                        Log.e(tag, "$message")
                        return
                    }
                    Log.e(tag, "$message", throwable)
                }
                VERBOSE -> {
                    if (throwable == null) {
                        Log.v(tag, "$message")
                        return
                    }
                    Log.v(tag, "$message", throwable)
                }
                ASSERT -> {
                    if (throwable == null) {
                        Log.wtf(tag, "$message")
                        return
                    }
                    Log.wtf(tag, "$message", throwable)
                }
            }
        }
    }
}

fun isReleaseBuild() = BuildConfig.BUILD_TYPE.toLowerCase(Locale.ROOT).contains(Regex("^release$"))

/*
fun <T: Service> isServiceRunning(context: Context, clazz: Class<T>): Boolean {
    val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (clazz.name == service.service.className) return true
    }
    return false
}*/
