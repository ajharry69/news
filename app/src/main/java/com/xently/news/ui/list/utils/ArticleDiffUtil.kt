package com.xently.news.ui.list.utils

import androidx.recyclerview.widget.DiffUtil
import com.xently.models.Article

object ArticleDiffUtil : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
}