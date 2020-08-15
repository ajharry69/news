package com.xently.news.ui.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class ArticleViewModel @ViewModelInject constructor(private val repository: IArticlesRepository) :
    ViewModel() {
    private val _articleFetchResult = MutableLiveData<TaskResult<Article>>()
    val articleFetchResult: LiveData<TaskResult<Article>>
        get() = _articleFetchResult
    private val _addBookmarkResult = MutableLiveData<TaskResult<Boolean>>()
    val addBookmarkResult: LiveData<TaskResult<Boolean>>
        get() = _addBookmarkResult
    private val _showProgressbar = MutableLiveData<Boolean>(false)
    val showProgressbar: LiveData<Boolean>
        get() = _showProgressbar
    val dataSource = ConflatedBroadcastChannel(LOCAL)
    val articleId = ConflatedBroadcastChannel<Long>()

    @OptIn(FlowPreview::class)
    val article: LiveData<Article>
        get() = articleId.asFlow().combineTransform(dataSource.asFlow()) { id, source ->
            emitAll(repository.getObservableArticle(id, source))
        }.catch {
            val (id, source) = Pair(articleId.valueOrNull, dataSource.valueOrNull ?: LOCAL)
            if (id != null && source == LOCAL) getArticle(id)
        }.asLiveData()

    private val taskResultObserver: (TaskResult<Any>) -> Unit = {
        _showProgressbar.value = it is TaskResult.Loading
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

    /**
     * Fetch article remotely
     */
    fun getArticle(id: Long) {
        _articleFetchResult.value = TaskResult.Loading
        viewModelScope.launch {
            _articleFetchResult.value = repository.getArticle(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _articleFetchResult.removeObserver(taskResultObserver)
        _addBookmarkResult.removeObserver(taskResultObserver)
    }
}