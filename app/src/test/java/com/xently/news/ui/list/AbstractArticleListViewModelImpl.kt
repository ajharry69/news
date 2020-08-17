package com.xently.news.ui.list

import android.app.Application
import com.xently.news.data.repository.IArticlesRepository

class AbstractArticleListViewModelImpl(app: Application, repository: IArticlesRepository) :
    AbstractArticleListViewModel(app, repository)