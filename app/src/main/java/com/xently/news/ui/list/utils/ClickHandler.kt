package com.xently.news.ui.list.utils

import com.xently.news.ui.list.AbstractArticleListViewModel

fun onRetryButtonClick(viewModel: AbstractArticleListViewModel) {
    // TODO: Initiate retry by passing retry request to the viewModel instead of below...
    viewModel.getArticles(enableLimits = false)
}