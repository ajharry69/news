package com.xently.news.ui.list

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.errorMessage
import com.xently.news.R
import com.xently.news.data.model.Article
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch

abstract class AbstractArticleListViewModel internal constructor(
    protected val repository: IArticlesRepository,
    app: Application
) : AndroidViewModel(app) {
    private val context = app.applicationContext
    private val _articleListsResults = MutableLiveData<TaskResult<List<Article>>>()
    val articleListsResults: LiveData<TaskResult<List<Article>>>
        get() = _articleListsResults
    private val _showProgressbar = MutableLiveData<Boolean>(false)
    val showProgressbar: LiveData<Boolean>
        get() = _showProgressbar
    private val _filteredResultsCount = MutableLiveData(0)
    val filteredResultsCount: LiveData<Int>
        get() = _filteredResultsCount
    private val _statusMessage = MutableLiveData<String>(context.getString(R.string.status_loading))

    /**
     * returns string resource to be shown as message on UI to update user/client on the
     * (list)data status
     */
    val statusMessage: LiveData<String>
        get() = _statusMessage

    init {
        _articleListsResults.observeForever {
            when (it) {
                is TaskResult.Success -> {
                    _showProgressbar.value = false
                    val message = if (it.data.isNullOrEmpty()) R.string.status_articles_empty
                    else R.string.status_loading
                    _statusMessage.value = context.getString(message)
                }
                is TaskResult.Error -> {
                    _showProgressbar.value = false
                    _statusMessage.value = it.errorMessage
                }
                is TaskResult.Loading -> {
                    _showProgressbar.value = true
                    _statusMessage.value =
                        context.getString(R.string.status_fetching_remote_articles)
                }
            }
        }
    }

    fun getArticles(searchQuery: String? = null) {
        _articleListsResults.value = TaskResult.Loading
        viewModelScope.launch {
            updateStatusMessageOnSearch(searchQuery, R.string.status_searching_remote_articles)
            _articleListsResults.value = repository.getArticles(searchQuery)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getObservableArticles(
        searchQuery: String? = null,
        source: Source = Source.LOCAL
    ): LiveData<List<Article>> {
        val articleLists = liveData<List<Article>> {
            updateStatusMessageOnSearch(searchQuery)
            val articles = repository.getObservableArticles(searchQuery, source).onEmpty {
                // fetch articles from internet if is empty
                // TODO: Make sure it does not result to an infinite loop
                getArticles(searchQuery)
            }
            emitSource(articles.asLiveData())
        }
        return Transformations.map(articleLists) {
            _filteredResultsCount.value = it.size
            it
        }
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