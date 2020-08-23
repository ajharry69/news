package com.xently.articles.comments.ui

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xently.articles.comments.R
import com.xently.articles.comments.data.repository.ICommentsRepository
import com.xently.common.data.Source
import com.xently.common.data.TaskResult
import com.xently.common.data.errorMessage
import com.xently.models.Comment
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CommentsViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: ICommentsRepository
) : AndroidViewModel(app) {

    private val context: Context = app.applicationContext
    private var commentListFetchRetryCount = START_TRY_COUNT
    private val _commentListResults = MutableLiveData<TaskResult<List<Comment>>>()
    val commentListResults: LiveData<TaskResult<List<Comment>>>
        get() = _commentListResults
    private val _sendCommentResults = MutableLiveData<TaskResult<List<Comment>>>()
    val sendCommentResults: LiveData<TaskResult<List<Comment>>>
        get() = _sendCommentResults
    private val _commentListCount = MutableLiveData(0)
    val commentListCount: LiveData<Int>
        get() = _commentListCount
    private val _showProgressbar = MutableLiveData(false)
    val showProgressbar: LiveData<Boolean>
        get() = _showProgressbar
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
            }.combine(commentLists.asFlow().conflate()) { showStatus, comments ->
                showStatus && comments.isNullOrEmpty()
            }.asLiveData()

    val articleId = ConflatedBroadcastChannel<Long>()
    val startCommentListRefresh = ConflatedBroadcastChannel(false)
    val searchQuery = ConflatedBroadcastChannel<String?>(null)
    val dataSource = ConflatedBroadcastChannel(Source.LOCAL)

    @OptIn(FlowPreview::class)
    private val _startCommentListRefresh: LiveData<Boolean>
        get() = startCommentListRefresh.asFlow().combine(searchQuery.asFlow()) { refresh, query ->
            if (refresh) getComments(articleId.valueOrNull, query, false)
            refresh
        }.asLiveData()

    @OptIn(FlowPreview::class)
    val commentLists: LiveData<List<Comment>>
        get() = articleId.asFlow()
            .combineTransform(searchQuery.asFlow()) { articleId, query ->
                val source = dataSource.value
                val message = when (source) {
                    Source.REMOTE -> R.string.status_searching_remote_comments
                    Source.LOCAL -> R.string.status_searching_comments
                }
                setStatusMessage(query, message)
                emitAll(repository.getObservableComments(articleId, query, source))
            }.conflate().asLiveData()

    private val observerCommentListTaskResult: (TaskResult<List<Comment>>) -> Unit = {
        when (it) {
            is TaskResult.Success -> {
                hideLoadingStatus()
                if (it.data.isNullOrEmpty()) {
                    setStatusMessage(R.string.status_comments_empty)
                } else {
                    commentListFetchRetryCount = START_TRY_COUNT
                    setStatusMessage(null)
                }
            }
            is TaskResult.Error -> {
                hideLoadingStatus()
                setStatusMessage(it.errorMessage)
            }
            is TaskResult.Loading -> {
                setShowProgressbar(true)
                val showHrPb = (commentLists.value?.size ?: commentListCount.value ?: 0) > 0
                setShowHorizontalProgressbar(showHrPb)
                setStatusMessage(R.string.status_fetching_remote_comments)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private val observerCommentList: (List<Comment>) -> Unit = {
        if (it.isNullOrEmpty()) {
            // initiate a remote data fetch if local data source returned empty list
            if (dataSource.value == Source.LOCAL) getComments(
                articleId.valueOrNull,
                searchQuery.value
            )
        }
        _commentListCount.value = it.size
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

    private val observerStartCommentListRefresh: (Boolean) -> Unit = {
        setShowSwipeRefreshProgressIndicator(it)
    }

    init {
        commentLists.observeForever(observerCommentList)
        _commentListResults.observeForever(observerCommentListTaskResult)
        _startCommentListRefresh.observeForever(observerStartCommentListRefresh)
        _showHorizontalProgressbar.observeForever(observerShowHorizontalProgressbar)
        _showSwipeRefreshProgressIndicator.observeForever(observerShowSwipeRefreshProgressIndicator)
    }

    /**
     * Fetch comment(s) remotely
     */
    fun getComments(
        articleId: Long? = null,
        searchQuery: String? = null,
        enableLimits: Boolean = true
    ) {
        if (!enableLimits) commentListFetchRetryCount = START_TRY_COUNT
        if (commentListFetchRetryCount > MAX_RETRY_COUNT) return
        _commentListResults.postValue(TaskResult.Loading)
        val query = searchQuery ?: this.searchQuery.valueOrNull
        val aId = articleId ?: this.articleId.value
        setStatusMessage(query, R.string.status_searching_remote_comments)
        viewModelScope.launch {
            _commentListResults.postValue(repository.getComments(aId, query))
            commentListFetchRetryCount++
        }
    }

    fun sendComment(comment: Comment) {
        _sendCommentResults.postValue(TaskResult.Loading)
        viewModelScope.launch {
            _sendCommentResults.postValue(repository.addComments(comment))
        }
    }

    fun setShowProgressbar(show: Boolean = false) {
        _showProgressbar.postValue(show)
    }

    fun setShowHorizontalProgressbar(show: Boolean = false) {
        _showHorizontalProgressbar.postValue(show)
    }

    /**
     * **Use is highly discouraged!** Use [startCommentListRefresh] by calling `offer` or `send` instead
     */
    fun setShowSwipeRefreshProgressIndicator(show: Boolean = false) {
        _showSwipeRefreshProgressIndicator.postValue(show)
    }

    fun setStatusMessage(message: String? = null) {
        if (commentLists.value.isNullOrEmpty()) _statusMessage.postValue(message)
    }

    fun setStatusMessage(@StringRes message: Int) = setStatusMessage(context.getString(message))

    override fun onCleared() {
        super.onCleared()
        commentLists.removeObserver(observerCommentList)
        _commentListResults.removeObserver(observerCommentListTaskResult)
        _showSwipeRefreshProgressIndicator.removeObserver(observerShowSwipeRefreshProgressIndicator)
        _startCommentListRefresh.removeObserver(observerStartCommentListRefresh)
        _showHorizontalProgressbar.removeObserver(observerShowHorizontalProgressbar)
    }

    private fun hideLoadingStatus() {
        startCommentListRefresh.offer(false)
        setShowHorizontalProgressbar(false)
        setShowProgressbar(false)
    }

    private fun setStatusMessage(
        query: String?,
        @StringRes message: Int = R.string.status_searching_comments
    ) {
        if (!query.isNullOrBlank()) setStatusMessage(message)
    }

    companion object {
        private const val START_TRY_COUNT = 1
        private const val MAX_RETRY_COUNT = 3
    }
}