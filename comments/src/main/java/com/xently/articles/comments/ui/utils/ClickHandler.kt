package com.xently.articles.comments.ui.utils

import com.xently.articles.comments.ui.CommentsViewModel

fun onRetryButtonClicked(viewModel: CommentsViewModel) {
    viewModel.getComments(enableLimits = false)
}