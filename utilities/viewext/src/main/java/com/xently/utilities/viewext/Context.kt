package com.xently.utilities.viewext

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat

@ColorInt
fun Context.getThemedColor(@AttrRes themeResId: Int): Int {
    val a = obtainStyledAttributes(null, intArrayOf(themeResId))
    try {
        return a.getColor(0, 9)
    } finally {
        a.recycle()
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Drawable.tintDrawable(@ColorInt tint: Int): Drawable {
    return DrawableCompat.wrap(this).mutate().apply {
        setTint(tint)
    }
}

fun Context.isDarkTheme(): Boolean = resources.configuration.uiMode and
        Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES