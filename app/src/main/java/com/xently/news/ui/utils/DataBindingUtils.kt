package com.xently.news.ui.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipGroup.LayoutParams.WRAP_CONTENT
import com.xently.utilities.viewext.hideViews
import com.xently.utilities.viewext.showViews

data class ChipData(val text: String, @DrawableRes val icon: Int? = null)

@BindingAdapter(value = ["android:enabled"])
fun setEnabled(view: View, enabled: Boolean = false) {
    view.isEnabled = enabled
}

@BindingAdapter(value = ["isVisible"])
fun setVisible(view: View, isVisible: Boolean) {
    if (isVisible) showViews(view) else hideViews(view)
//    view.isVisible = isVisible
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

@BindingAdapter(value = ["chipItems"])
fun setChips(group: ChipGroup, chipItems: Collection<ChipData>) {
    group.removeAllViews()
    chipItems.forEach {
        val chip = Chip(group.context).apply {
            text = it.text

            chipIcon = it.icon?.let { icon -> ContextCompat.getDrawable(context, icon) }
        }
        group.addView(chip, WRAP_CONTENT, WRAP_CONTENT)
    }
}