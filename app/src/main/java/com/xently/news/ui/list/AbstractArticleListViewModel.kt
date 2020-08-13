package com.xently.news.ui.list

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.TaskResult
import com.xently.common.data.errorMessage
import com.xently.news.R
import com.xently.news.data.model.Article
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class AbstractArticleListViewModel internal constructor(
    protected val repository: IArticlesRepository,
    app: Application
) : AndroidViewModel(app) {

    @VisibleForTesting
    val context: Context = app.applicationContext
    private val _articleListsResults = MutableLiveData<TaskResult<List<Article>>>()
    val articleListsResults: LiveData<TaskResult<List<Article>>>
        get() = _articleListsResults
    private val _showProgressbar = MutableLiveData<Boolean>(false)
    val showProgressbar: LiveData<Boolean>
        get() = _showProgressbar
    private val _articleListCount = MutableLiveData(0)
    val articleListCount: LiveData<Int>
        get() = _articleListCount
    private val _statusMessage = MutableLiveData<String>(context.getString(R.string.status_loading))

    /**
     * returns string resource to be shown as message on UI to update user/client on the
     * (list)data status
     */
    val statusMessage: LiveData<String>
        get() = _statusMessage

    val searchQuery = ConflatedBroadcastChannel<String?>(null)
    val dataSource = ConflatedBroadcastChannel(LOCAL)

    @OptIn(FlowPreview::class)
    val articleLists: LiveData<List<Article>>
        get() = searchQuery.asFlow()
            .combineTransform(dataSource.asFlow()) { query, source ->
                val message = when (source) {
                    REMOTE -> R.string.status_searching_remote_articles
                    LOCAL -> R.string.status_searching_articles
                }
                updateStatusMessageOnSearch(query, message)
                emitAll(repository.getObservableArticles(query, source))
            }.conflate().asLiveData()

    private val articleListTaskResultsObserver: (TaskResult<List<Article>>) -> Unit = {
        when (it) {
            is TaskResult.Success -> {
                _showProgressbar.postValue(false)
                val message = if (it.data.isNullOrEmpty()) R.string.status_articles_empty
                else R.string.status_loading
                _statusMessage.postValue(context.getString(message))
            }
            is TaskResult.Error -> {
                _showProgressbar.postValue(false)
                _statusMessage.postValue(it.errorMessage)
            }
            is TaskResult.Loading -> {
                _showProgressbar.postValue(true)
                _statusMessage.postValue(context.getString(R.string.status_fetching_remote_articles))
            }
        }
    }

    @OptIn(FlowPreview::class)
    private val observableArticleListObserver: (List<Article>) -> Unit = {
        if (it.isNullOrEmpty()) {
            // initiate a remote data fetch if local data source returned empty list
            viewModelScope.launch {
                searchQuery.asFlow().collect { query ->
                    if (dataSource.value == LOCAL) getArticles(query)
                }
            }
        }
        _articleListCount.value = it.size
    }

    init {
        _articleListsResults.observeForever(articleListTaskResultsObserver)
        articleLists.observeForever(observableArticleListObserver)
    }

    /**
     * Fetch article(s) remotely
     */
    fun getArticles(searchQuery: String? = null) {
        _articleListsResults.postValue(TaskResult.Loading)
        updateStatusMessageOnSearch(searchQuery, R.string.status_searching_remote_articles)
        viewModelScope.launch {
            _articleListsResults.postValue(repository.getArticles(searchQuery))
        }
    }

    override fun onCleared() {
        articleLists.removeObserver(observableArticleListObserver)
        _articleListsResults.removeObserver(articleListTaskResultsObserver)
        super.onCleared()
    }

    private fun updateStatusMessageOnSearch(
        searchQuery: String?,
        @StringRes message: Int = R.string.status_searching_articles
    ) {
        if (!searchQuery.isNullOrBlank()) {
            // search initiation is expected
            _statusMessage.value = context.getString(message)
        }
    }
}