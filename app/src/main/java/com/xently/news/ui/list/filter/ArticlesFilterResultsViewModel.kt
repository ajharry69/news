package com.xently.news.ui.list.filter

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.ui.list.AbstractArticleListViewModel

class ArticlesFilterResultsViewModel @ViewModelInject constructor(
    repository: IArticlesRepository,
    app: Application
) : AbstractArticleListViewModel(repository, app) {
    // TODO: Implement the ViewModel
}