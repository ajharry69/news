package com.xently.common.utils

import com.google.android.material.textfield.TextInputLayout
import com.xently.common.R
import com.xently.utilities.viewext.setErrorTextAndFocus
import com.xently.utilities.viewext.textAsString

fun TextInputLayout.showErrorOnInvalidNumber(threshold: Float = 0f): Float? {
    val number = editText?.textAsString()?.toFloatOrNull()
    if (number == null) {
        setErrorTextAndFocus(R.string.invalid_number)
        return null
    }
    if (number <= threshold) {
        setErrorTextAndFocus(R.string.value_below_threshold, threshold)
        return null
    }
    return number
}

fun TextInputLayout.showErrorOnNullOrBlankString(): String? {
    val value = editText?.textAsString()
    if (value.isNullOrBlank()) {
        setErrorTextAndFocus(R.string.required_field)
        return null
    }
    return value
}