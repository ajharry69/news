package com.xently.news.ui.list.utils

import android.view.View
import com.xently.news.data.model.Article

interface OnActionButtonClickListener {
    fun onActionButtonClick(article: Article, view: View)
}