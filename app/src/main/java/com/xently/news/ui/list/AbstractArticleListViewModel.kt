package com.xently.news.ui.list

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.TaskResult
import com.xently.common.data.errorMessage
import com.xently.models.Article
import com.xently.news.R
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.ui.AbstractArticleViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Suppress("PropertyName")
abstract class AbstractArticleListViewModel internal constructor(
    app: Application,
    private val repository: IArticlesRepository,
) : AbstractArticleViewModel(app, repository) {

    private val context: Context = app.applicationContext
    private var articleListFetchRetryCount = START_TRY_COUNT
    private val _articleListResults = MutableLiveData<TaskResult<List<Article>>>()
    val articleListResults: LiveData<TaskResult<List<Article>>>
        get() = _articleListResults
    private val _articleListCount = MutableLiveData(0)
    val articleListCount: LiveData<Int>
        get() = _articleListCount
    private val _showHorizontalProgressbar = MutableLiveData(false)
    val showHorizontalProgressbar: LiveData<Boolean>
        get() = _showHorizontalProgressbar
    private val _showSwipeRefreshProgressIndicator = MutableLiveData(false)
    val showSwipeRefreshProgressIndicator: LiveData<Boolean>
        get() = _showSwipeRefreshProgressIndicator
    private val _statusMessage = MutableLiveData<String>(null)

    /**
     * returns string resource to be shown as message on UI to update user/client on the
     * (list)data status
     */
    val statusMessage: LiveData<String>
        get() = _statusMessage

    /**
     * status view is view that shows (data) status message on the screen
     */
    val showStatusView: LiveData<Boolean>
        get() = _showProgressbar.asFlow().conflate()
            .combine(_statusMessage.asFlow().conflate()) { showProgress, statusMsg ->
                showProgress || !statusMsg.isNullOrBlank()
            }.combine(articleLists.asFlow().conflate()) { showStatus, articles ->
                showStatus && articles.isNullOrEmpty()
            }.asLiveData()

    val startArticleListRefresh = ConflatedBroadcastChannel(false)
    val searchQuery = ConflatedBroadcastChannel<String?>(null)
    val dataSource = ConflatedBroadcastChannel(LOCAL)

    @OptIn(FlowPreview::class)
    private val _startArticleListRefresh: LiveData<Boolean>
        get() = startArticleListRefresh.asFlow().combine(searchQuery.asFlow()) { refresh, query ->
            if (refresh) getArticles(query, false)
            refresh
        }.asLiveData()

    @OptIn(FlowPreview::class)
    val articleLists: LiveData<List<Article>>
        get() = searchQuery.asFlow()
            .combineTransform(dataSource.asFlow()) { query, source ->
                val message = when (source) {
                    REMOTE -> R.string.status_searching_remote_articles
                    LOCAL -> R.string.status_searching_articles
                }
                setStatusMessage(query, message)
                emitAll(repository.getObservableArticles(query, source))
            }.conflate().asLiveData()

    private val observerArticleListTaskResult: (TaskResult<List<Article>>) -> Unit = {
        when (it) {
            is TaskResult.Success -> {
                hideLoadingStatus()
                if (it.data.isNullOrEmpty()) {
                    setStatusMessage(R.string.status_articles_empty)
                } else {
                    articleListFetchRetryCount = START_TRY_COUNT
                    setStatusMessage(null)
                }
            }
            is TaskResult.Error -> {
                hideLoadingStatus()
                setStatusMessage(it.errorMessage)
            }
            is TaskResult.Loading -> {
                setShowProgressbar(true)
                val showHrPb = (articleLists.value?.size ?: articleListCount.value ?: 0) > 0
                setShowHorizontalProgressbar(showHrPb)
                setStatusMessage(R.string.status_fetching_remote_articles)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private val observerArticleList: (List<Article>) -> Unit = {
        if (it.isNullOrEmpty()) {
            // initiate a remote data fetch if local data source returned empty list
            if (dataSource.value == LOCAL) getArticles(searchQuery.value)
        }
        _articleListCount.value = it.size
    }

    private val observerShowHorizontalProgressbar: (Boolean) -> Unit = {
        // do not show two progress bars at the same time
        // Only show progress bar if(ALL MUST BE MET):
        //      1. horizontal progress bar is not being shown(it = false)
        //      2. show progress bar was requested before(value = true)
        setShowProgressbar(!it && showProgressbar.value ?: false)
    }

    private val observerShowSwipeRefreshProgressIndicator: (Boolean) -> Unit = {
        // do not show horizontal progress bar and swipe refresh indicator at the same time
        // Only show horizontal progress bar if(ALL MUST BE MET):
        //      1. swipe refresh indicator is not being shown(it = false)
        //      2. show horizontal progress bar was requested before(value = true)
        setShowHorizontalProgressbar(!it && _showHorizontalProgressbar.value ?: false)
    }

    private val observerStartArticleListRefresh: (Boolean) -> Unit = {
        setShowSwipeRefreshProgressIndicator(it)
    }

    init {
        articleLists.observeForever(observerArticleList)
        _articleListResults.observeForever(observerArticleListTaskResult)
        _startArticleListRefresh.observeForever(observerStartArticleListRefresh)
        _showHorizontalProgressbar.observeForever(observerShowHorizontalProgressbar)
        _showSwipeRefreshProgressIndicator.observeForever(observerShowSwipeRefreshProgressIndicator)
    }

    /**
     * Fetch article(s) remotely
     */
    fun getArticles(searchQuery: String? = null, enableLimits: Boolean = true) {
        if (!enableLimits) articleListFetchRetryCount = START_TRY_COUNT
        if (articleListFetchRetryCount > MAX_RETRY_COUNT) return
        _articleListResults.postValue(TaskResult.Loading)
        val query = searchQuery ?: this.searchQuery.valueOrNull
        setStatusMessage(query, R.string.status_searching_remote_articles)
        viewModelScope.launch {
            _articleListResults.postValue(repository.getArticles(query, enableLimits))
            articleListFetchRetryCount++
        }
    }

    fun setShowHorizontalProgressbar(show: Boolean = false) {
        _showHorizontalProgressbar.postValue(show)
    }

    /**
     * **Use is highly discouraged!** Use [startArticleListRefresh] by calling `offer` or `send` instead
     */
    fun setShowSwipeRefreshProgressIndicator(show: Boolean = false) {
        _showSwipeRefreshProgressIndicator.postValue(show)
    }

    fun setStatusMessage(message: String? = null) {
        if (articleLists.value.isNullOrEmpty()) _statusMessage.postValue(message)
    }

    fun setStatusMessage(@StringRes message: Int) = setStatusMessage(context.getString(message))

    override fun onTaskResultsReceived(results: TaskResult<Any>) {
        setShowHorizontalProgressbar(results is TaskResult.Loading)
    }

    override fun onCleared() {
        super.onCleared()
        articleLists.removeObserver(observerArticleList)
        _articleListResults.removeObserver(observerArticleListTaskResult)
        _showSwipeRefreshProgressIndicator.removeObserver(observerShowSwipeRefreshProgressIndicator)
        _startArticleListRefresh.removeObserver(observerStartArticleListRefresh)
        _showHorizontalProgressbar.removeObserver(observerShowHorizontalProgressbar)
    }

    private fun hideLoadingStatus() {
        startArticleListRefresh.offer(false)
        setShowHorizontalProgressbar(false)
        setShowProgressbar(false)
    }

    private fun setStatusMessage(
        query: String?,
        @StringRes message: Int = R.string.status_searching_articles,
    ) {
        if (!query.isNullOrBlank()) setStatusMessage(message)
    }

    companion object {
        private const val START_TRY_COUNT = 1
        private const val MAX_RETRY_COUNT = 3
    }
}