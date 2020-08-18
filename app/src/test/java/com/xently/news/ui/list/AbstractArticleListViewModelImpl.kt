package com.xently.news.ui.list

import android.app.Application
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AbstractArticleListViewModelImpl(
    app: Application,
    repository: IArticlesRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : AbstractArticleListViewModel(app, repository)