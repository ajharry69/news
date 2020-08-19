package com.xently.news.ui.list.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xently.news.R
import com.xently.news.data.model.Article
import com.xently.news.databinding.ArticleItemBinding
import com.xently.news.ui.details.ArticleFragmentArgs

class ArticlesAdapter constructor(private val clickListener: OnActionButtonClickListener? = null) :
    ListAdapter<Article, ArticlesAdapter.ViewHolder>(ArticleDiffUtil()) {
    interface OnActionButtonClickListener {
        fun onActionButtonClick(article: Article, view: View)
    }

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
                root.setOnClickListener {
                    val bundle = ArticleFragmentArgs(article.id).toBundle()
                    it.findNavController().navigate(
                        R.id.dest_article_details,
                        bundle,
                        navOptions {
                            launchSingleTop = true
                        }
                    )
                }
                share.setOnClickListener {
                    clickListener?.onActionButtonClick(article, it)
                }
                addBookmark.setOnClickListener {
                    clickListener?.onActionButtonClick(article, it)
                }
                addComment.setOnClickListener {
                    clickListener?.onActionButtonClick(article, it)
                }
            }
        }
    }
}