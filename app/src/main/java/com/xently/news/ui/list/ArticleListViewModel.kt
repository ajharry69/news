package com.xently.news.ui.list

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.xently.news.data.repository.IArticlesRepository

class ArticleListViewModel @ViewModelInject constructor(
    repository: IArticlesRepository,
    app: Application
) : AbstractArticleListViewModel(repository, app) {
    // TODO: Implement the ViewModel
}