package com.xently.news.ui.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.findFragment
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipGroup.LayoutParams.WRAP_CONTENT
import com.xently.media.ui.MediaFragment

data class ChipData(val text: String, @DrawableRes val icon: Int? = null)

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

@BindingAdapter(value = ["chipItems"])
fun setChips(group: ChipGroup, chipItems: Collection<ChipData>) {
    chipItems.forEach {
        val chip = Chip(group.context).apply {
            text = it.text
            chipIcon = it.icon?.let { icon -> context.getDrawable(icon) }
        }
        group.addView(chip, WRAP_CONTENT, WRAP_CONTENT)
    }
}

@BindingAdapter(value = ["mediaUris"])
fun setMediaFromUris(view: FragmentContainerView, mediaUris: List<String>) {
    try {
        view.findFragment<MediaFragment>().apply {
            setMediaUris(*mediaUris.toTypedArray())
        }
    } catch (ex: IllegalStateException) {
        // ignore
    }
}