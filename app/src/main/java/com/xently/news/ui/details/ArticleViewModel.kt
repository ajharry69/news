package com.xently.news.ui.details

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.TaskResult
import com.xently.news.data.model.Article
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.ui.AbstractArticleViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class ArticleViewModel @ViewModelInject constructor(
    private val repository: IArticlesRepository,
    app: Application
) : AbstractArticleViewModel(repository, app) {
    private val _articleFetchResult = MutableLiveData<TaskResult<Article>>()
    val articleFetchResult: LiveData<TaskResult<Article>>
        get() = _articleFetchResult
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

    init {
        _articleFetchResult.observeForever(taskResultObserver)
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
    }
}