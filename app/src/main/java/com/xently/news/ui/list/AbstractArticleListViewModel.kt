package com.xently.news.ui.list

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.TaskResult
import com.xently.models.Article
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.ui.AbstractArticleViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@Suppress("PropertyName")
abstract class AbstractArticleListViewModel internal constructor(
    app: Application,
    private val repository: IArticlesRepository,
) : AbstractArticleViewModel(app, repository) {

    private val context: Context = app.applicationContext
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
            }.asLiveData()

    val startArticleListRefresh = ConflatedBroadcastChannel(false)
    val searchQuery = ConflatedBroadcastChannel<String?>(null)
    val dataSource = ConflatedBroadcastChannel(LOCAL)

    private val _startArticleListRefresh: LiveData<Boolean>
        get() = startArticleListRefresh.asFlow().asLiveData()

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
        _startArticleListRefresh.observeForever(observerStartArticleListRefresh)
        _showHorizontalProgressbar.observeForever(observerShowHorizontalProgressbar)
        _showSwipeRefreshProgressIndicator.observeForever(observerShowSwipeRefreshProgressIndicator)
    }

    fun getObservableArticles(enablePlaceholders: Boolean = false): Flow<PagingData<Article>> =
        searchQuery.asFlow().flatMapLatest {
            repository.getArticles(searchQuery = it, enablePlaceholders = enablePlaceholders)
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
        _statusMessage.postValue(message)
    }

    fun setStatusMessage(@StringRes message: Int) = setStatusMessage(context.getString(message))

    override fun onTaskResultsReceived(results: TaskResult<Any>) {
        setShowHorizontalProgressbar(results is TaskResult.Loading)
    }

    override fun onCleared() {
        super.onCleared()
        _showSwipeRefreshProgressIndicator.removeObserver(observerShowSwipeRefreshProgressIndicator)
        _startArticleListRefresh.removeObserver(observerStartArticleListRefresh)
        _showHorizontalProgressbar.removeObserver(observerShowHorizontalProgressbar)
    }
}