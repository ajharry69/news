package com.xently.news.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xently.common.data.TaskResult
import com.xently.news.data.repository.IArticlesRepository
import kotlinx.coroutines.launch

@Suppress("PropertyName")
abstract class AbstractArticleViewModel internal constructor(
    app: Application,
    private val repository: IArticlesRepository
) : AndroidViewModel(app) {
    private val _addBookmarkResult = MutableLiveData<TaskResult<Boolean>>()
    val addBookmarkResult: LiveData<TaskResult<Boolean>>
        get() = _addBookmarkResult
    protected val _showProgressbar = MutableLiveData(false)
    val showProgressbar: LiveData<Boolean>
        get() = _showProgressbar

    protected val taskResultObserver: (TaskResult<Any>) -> Unit = {
        _showProgressbar.value = it is TaskResult.Loading
    }

    init {
        _addBookmarkResult.observeForever(taskResultObserver)
    }

    fun addBookmark(articleId: Long, bookmark: Boolean) {
        _addBookmarkResult.value = TaskResult.Loading
        viewModelScope.launch {
            _addBookmarkResult.value = repository.addBookMark(articleId, bookmark)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _addBookmarkResult.removeObserver(taskResultObserver)
    }

    fun setShowProgressbar(show: Boolean = false) {
        _showProgressbar.postValue(show)
    }
}