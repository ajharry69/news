package com.xently.news.ui.list.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xently.news.data.model.Article
import com.xently.news.databinding.ArticleItemBinding
import javax.inject.Inject

class ArticlesAdapter @Inject constructor() :
    ListAdapter<Article, ArticlesAdapter.ViewHolder>(ArticleDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ArticleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.run {
                this.article = article
                /*root.setOnClickListener {
                    onArticleItemClick(it, article)
                }
                share.setOnClickListener {
                    onShareClick(it, article)
                }
                addBookmark.setOnClickListener {
                    onAddBookmarkClick(it, article)
                }*/
            }
        }
    }
}