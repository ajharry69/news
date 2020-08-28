package com.xently.news.ui.list.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xently.models.Article

class ArticlesAdapter constructor(private val clickListener: OnActionButtonClickListener? = null) :
    ListAdapter<Article, ArticleViewHolder>(ArticleDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArticleViewHolder.create(parent, clickListener)

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}