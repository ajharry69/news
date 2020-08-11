package com.xently.news.ui.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter(value = ["isVisible"])
fun setVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter(value = ["imageFromUrl", "placeholder"], requireAll = false)
fun setImageFromUrl(view: ImageView, url: String?, placeholder: Drawable?) {
    if (url == null) {
        view.setImageDrawable(placeholder)
        return
    }
    Glide.with(view.context)
        .load(url)
        .centerCrop()
        .placeholder(placeholder)
        .into(view)
}