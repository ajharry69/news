package com.xently.common.utils

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Will be used in actual code to track whether long running tasks in the application
 * are still working. It's made singleton to make it usable from anywhere in the code
 */
object EspressoIdlingResource {
    private const val GLOBAL = "global"

    /**
     * Enables incrementing and decrementing a counter. i.e when counter > 0 the app
     * is considered working and when the counter is less than 0, the app is considered
     * idle
     */
    @JvmField
    val COUNTING_IDLING_RESOURCE = CountingIdlingResource(GLOBAL)

    fun increment() {
        COUNTING_IDLING_RESOURCE.increment()
    }

    fun decrement() {
        if (!COUNTING_IDLING_RESOURCE.isIdleNow) {
            COUNTING_IDLING_RESOURCE.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment() // the app is busy
    return try {
        function.invoke()
    } finally {
        EspressoIdlingResource.decrement() // the app is idle
    }
}