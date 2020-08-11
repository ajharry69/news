package com.xently.news.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.xently.news.data.model.Article

fun startShareArticleIntent(context: Context, article: Article) {
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