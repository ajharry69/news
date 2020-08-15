package com.xently.news.ui.list.utils

import android.content.res.ColorStateList
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.xently.news.R
import com.xently.utilities.viewext.getThemedColor

@BindingAdapter(value = ["isBookmarked"])
fun switchTintAndContentDescriptionOnBookmark(view: ImageButton, isBookmarked: Boolean = false) {
    view.apply {
        val (contentDesc, iconTint) = if (isBookmarked) {
            Pair(R.string.remove_bookmark, R.attr.colorControlActivated)
        } else Pair(R.string.add_bookmark, R.attr.colorControlNormal)
        imageTintList = ColorStateList.valueOf(context.getThemedColor(iconTint))
        contentDescription = context.getString(contentDesc)
    }
}