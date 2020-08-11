package com.xently.news.ui.list.utils

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.navigation.findNavController
import com.xently.news.data.model.Article
import com.xently.news.ui.list.ArticleListFragmentDirections.Companion.actionDestArticleListToDestArticleDetails
import com.xently.utilities.viewext.showSnackBar

fun onShareClick(view: View, article: Article) {
    view.setOnClickListener {
        val context = it.context
        val shareIntent = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, article.url)
                putExtra(Intent.EXTRA_TITLE, article.headline)
                if (!article.mediaThumbnailUrl.isNullOrBlank()) {
                    data = Uri.parse(article.mediaThumbnailUrl)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
            }, null
        )
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(shareIntent)
        }
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