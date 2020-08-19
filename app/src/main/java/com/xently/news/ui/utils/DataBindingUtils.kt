package com.xently.news.ui.utils

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipGroup.LayoutParams.WRAP_CONTENT
import com.xently.models.util.ChipData

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