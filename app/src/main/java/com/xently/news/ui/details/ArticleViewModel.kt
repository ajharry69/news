package com.xently.news.ui.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch

class ArticleViewModel @ViewModelInject constructor(private val repository: IArticlesRepository) :
    ViewModel() {
    private val _articleFetchResult = MutableLiveData<TaskResult<Article>>()
    val articleFetchResult: LiveData<TaskResult<Article>>
        get() = _articleFetchResult
    private val _article = MutableLiveData<Article>()
    val article: LiveData<Article>
        get() = _article
    private val _addBookmarkResult = MutableLiveData<TaskResult<Boolean>>()
    val addBookmarkResult: LiveData<TaskResult<Boolean>>
        get() = _addBookmarkResult
    private val _showProgressbar = MutableLiveData<Boolean>(false)
    val showProgressbar: LiveData<Boolean>
        get() = _showProgressbar

    fun addBookmark(articleId: Long, bookmark: Boolean) {
        _addBookmarkResult.value = TaskResult.Loading
        viewModelScope.launch {
            _addBookmarkResult.value = repository.addBookMark(articleId, bookmark)
        }
    }

    @JvmOverloads
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getObservableArticle(id: Long, source: Source = Source.LOCAL): LiveData<Article> {
        val observableArticle = liveData<Article> {
            val article = repository.getObservableArticle(id, source).onEmpty {
                _articleFetchResult.value = TaskResult.Loading
                _articleFetchResult.value = repository.getArticle(id)
            }
            emitSource(article.asLiveData())
        }
        return Transformations.map(observableArticle) {
            _article.value = it
            it
        }
    }
}