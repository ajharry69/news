package com.xently.common.utils

import android.graphics.BitmapFactory
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.textfield.TextInputLayout
import com.xently.common.R
import com.xently.utilities.viewext.setErrorTextAndFocus

/**
 * scales down the image contained in [imagePath] to the size of [ImageView] and sets
 * it to [this@setImageFromPath]
 * @param imagePath absolute file path of image to be shown on [this@setImageFromPath]
 * ([ImageView])
 */
fun ImageView.setImageFromPath(imagePath: String) {
    val bmOptions = BitmapFactory.Options().apply {
        // Get the dimensions of the bitmap
        inJustDecodeBounds = true

        val photoW: Int = outWidth
        val photoH: Int = outHeight

        // Determine how much to scale down the image
        val scaleFactor: Int = (photoW / width).coerceAtMost(photoH / height)

        // Decode the image file into a Bitmap sized to fill the View
        inJustDecodeBounds = false
        inSampleSize = scaleFactor
        // inPurgeable = true
    }
    BitmapFactory.decodeFile(imagePath, bmOptions)?.also { bitmap ->
        setImageBitmap(bitmap)
    }
}

fun EditText.textAsString(): String? = text.let { if (it.isNullOrBlank()) null else it.toString() }

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