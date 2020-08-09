package com.xently.news.ui.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEmpty

class ArticleViewModel @ViewModelInject constructor(private val repository: IArticlesRepository) :
    ViewModel() {
    private val _articleFetchResult = MutableLiveData<TaskResult<Article>>()
    val articleFetchResult: LiveData<TaskResult<Article>>
        get() = _articleFetchResult

    @JvmOverloads
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getObservableArticle(id: Long, source: Source = Source.LOCAL): LiveData<Article> {
        return liveData {
            val article = repository.getObservableArticle(id, source).onEmpty {
                _articleFetchResult.value = TaskResult.Loading
                _articleFetchResult.value = repository.getArticle(id)
            }
            emitSource(article.asLiveData())
        }
    }
}