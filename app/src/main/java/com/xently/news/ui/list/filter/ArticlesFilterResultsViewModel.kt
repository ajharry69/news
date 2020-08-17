package com.xently.news.ui.list.filter

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.ui.list.AbstractArticleListViewModel

class ArticlesFilterResultsViewModel @ViewModelInject constructor(
    app: Application,
    repository: IArticlesRepository
) : AbstractArticleListViewModel(app, repository) {
    // TODO: Implement the ViewModel
}