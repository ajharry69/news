package com.xently.news.ui.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.flow.catch
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

    private val taskResultObserver: (TaskResult<Any>) -> Unit = {
        _showProgressbar.value = it is TaskResult.Loading
        if (it is TaskResult.Success && it.data is Article){
            _article.value = it.data as Article
        }
    }

    init {
        _articleFetchResult.observeForever(taskResultObserver)
        _addBookmarkResult.observeForever(taskResultObserver)
    }

    fun addBookmark(articleId: Long, bookmark: Boolean) {
        _addBookmarkResult.value = TaskResult.Loading
        viewModelScope.launch {
            _addBookmarkResult.value = repository.addBookMark(articleId, bookmark)
        }
    }

    @JvmOverloads
    fun getObservableArticle(id: Long, source: Source = Source.LOCAL): LiveData<Article> {
        val observableArticle = liveData<Article> {
            repository.getObservableArticle(id, source).onEmpty {
                getArticle(id)
            }.catch {
                getArticle(id)
            }
        }
        return Transformations.map(observableArticle) {
            // FIXME: 8/12/20 could be a repeat of Flow above
            if (it == null) getArticle(id)
            _article.value = it
            it
        }
    }

    fun getArticle(id: Long) {
        _articleFetchResult.value = TaskResult.Loading
        viewModelScope.launch {
            _articleFetchResult.value = repository.getArticle(id)
        }
    }

    override fun onCleared() {
        _articleFetchResult.removeObserver(taskResultObserver)
        _addBookmarkResult.removeObserver(taskResultObserver)
        super.onCleared()
    }
}