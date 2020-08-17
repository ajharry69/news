package com.xently.news.ui.list

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.xently.news.data.repository.IArticlesRepository

class ArticleListViewModel @ViewModelInject constructor(
    app: Application,
    repository: IArticlesRepository
) : AbstractArticleListViewModel(app, repository) {
    // TODO: Implement the ViewModel
}