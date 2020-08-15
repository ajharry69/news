package com.xently.news.ui.details

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter

@BindingAdapter("isHeightWrapContent")
fun setHeightWrapContent(view: View, isHeightWrapContent: Boolean = false) {
    view.updateLayoutParams {
        if (isHeightWrapContent) height = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}