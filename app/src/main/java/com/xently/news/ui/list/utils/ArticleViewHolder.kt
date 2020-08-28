package com.xently.news.ui.list.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.xently.models.Article
import com.xently.news.R
import com.xently.news.databinding.ArticleItemBinding
import com.xently.news.ui.details.ArticleFragmentArgs

class ArticleViewHolder(
    private val binding: ArticleItemBinding,
    private val clickListener: OnActionButtonClickListener? = null,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(article: Article?) {
        if (article == null) return
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
            flagInappropriate.setOnClickListener {
                clickListener?.onActionButtonClick(article, it)
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            clickListener: OnActionButtonClickListener? = null,
        ): ArticleViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ArticleItemBinding.inflate(layoutInflater, parent, false)
            return ArticleViewHolder(binding, clickListener)
        }
    }
}