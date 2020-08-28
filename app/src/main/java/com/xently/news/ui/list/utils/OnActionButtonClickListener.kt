package com.xently.news.ui.list.utils

import android.view.View
import com.xently.models.Article

fun interface OnActionButtonClickListener {
    fun onActionButtonClick(article: Article, view: View)
}