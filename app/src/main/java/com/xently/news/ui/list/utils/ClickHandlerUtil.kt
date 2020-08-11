package com.xently.news.ui.list.utils

import android.view.View
import androidx.navigation.findNavController
import com.xently.news.data.model.Article
import com.xently.news.ui.list.ArticleListFragmentDirections.Companion.actionDestArticleListToDestArticleDetails
import com.xently.news.ui.utils.startShareArticleIntent
import com.xently.utilities.viewext.showSnackBar

fun onShareClick(view: View, article: Article) {
    view.setOnClickListener {
        startShareArticleIntent(it.context, article)
    }
}

fun onAddBookmarkClick(view: View, article: Article) {
    view.setOnClickListener {
        showSnackBar(it, "Adding ${article.headline} to bookmark")
    }
}

fun onArticleItemClick(view: View, article: Article) {
    view.setOnClickListener {
        it.findNavController().navigate(actionDestArticleListToDestArticleDetails(article.id))
    }
}