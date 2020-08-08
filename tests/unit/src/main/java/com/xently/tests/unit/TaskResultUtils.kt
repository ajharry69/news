package com.xently.tests.unit

import com.xently.common.data.TaskResult
import com.xently.common.data.TaskResult.Error
import com.xently.common.data.TaskResult.Success
import com.xently.common.data.data
import com.xently.common.data.errorMessage
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*


fun <T> TaskResult<T?>.assertDataEqual(data: T?) {
    assertThat(this, instanceOf(Success::class.java))
    this as Success
    assertThat(this.data, equalTo(data))
}

inline fun <reified T> TaskResult<T?>.assertDataNotNullValue() {
    assertThat(this, instanceOf(Success::class.java))
    assertThat(data, notNullValue(T::class.java))
}

fun TaskResult<*>.assertErrorMessageEquals(message: String) {
    assertThat(this, instanceOf(Error::class.java))
    this as Error
    assertThat(errorMessage, equalToIgnoringCase(message))
}
