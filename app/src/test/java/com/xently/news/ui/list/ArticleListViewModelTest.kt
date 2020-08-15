package com.xently.news.ui.list

import android.app.Application
import android.os.Build
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xently.common.data.Source
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.TaskResult.Loading
import com.xently.news.ARTICLE
import com.xently.news.R
import com.xently.news.createArticles
import com.xently.news.data.repository.ArticlesRepository
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.fakes.FakeArticleDataSource
import com.xently.tests.unit.getOrAwaitValue
import com.xently.tests.unit.getValueOrAwaitFlowValue
import com.xently.tests.unit.rules.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ArticleListViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: IArticlesRepository
    private lateinit var viewModel: ArticleListViewModel

    @Before
    fun setUp() {
        val local = FakeArticleDataSource(ARTICLE)
        val remote = FakeArticleDataSource(*REMOTE_ARTICLES.toTypedArray(), ARTICLE)
        repository = ArticlesRepository(local, remote, Dispatchers.Unconfined)
        viewModel = ArticleListViewModel(
            repository,
            ApplicationProvider.getApplicationContext() as Application
        )
    }

    @Test
    fun showProgressbar() {
        // show progress bar is false by default
        assertThat(viewModel.showProgressbar.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun statusMessage() {
        // status message is loading by default
        assertThat(
            viewModel.statusMessage.getOrAwaitValue(),
            equalTo(viewModel.context.getString(R.string.status_loading))
        )
    }

    /**
     * showProgress & statusMessage LiveData are also updated depending on the returned results
     * i.e. `true` for showProgressbar when Loading is returned and false and when either
     * Success or Error is returned
     */
    @Test
    fun `getArticles immediately returns Loading TaskResult then reverts to Success or Error upon addBookmark completion`() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.getArticles()

        assertThat(viewModel.articleListResults.getOrAwaitValue(), instanceOf(Loading::class.java))
        assertThat(viewModel.showProgressbar.getOrAwaitValue(), equalTo(true))
        // status is updated to show that articles are being fetched from internet
        assertThat(
            viewModel.statusMessage.getOrAwaitValue(),
            equalToIgnoringCase(viewModel.context.getString(R.string.status_fetching_remote_articles))
        )

        mainCoroutineRule.resumeDispatcher()
        assertThat(
            viewModel.articleListResults.getOrAwaitValue(),
            not(instanceOf(Loading::class.java))
        )
        assertThat(viewModel.showProgressbar.getOrAwaitValue(), equalTo(false))
    }

    @Test
    fun `getArticle with a searchQuery provided sets appropriate statusMessage tailed for search`() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.getArticles("search query")

        assertThat(
            viewModel.statusMessage.getOrAwaitValue(),
            equalToIgnoringCase(viewModel.context.getString(R.string.status_searching_remote_articles))
        )
        mainCoroutineRule.resumeDispatcher()
    }

    @Test
    fun `getObservableArticles with a searchQuery provided sets appropriate statusMessage depending on the requested data source`() =
        runBlockingTest {
            assertStatusMessage(REMOTE, R.string.status_searching_remote_articles)
            assertStatusMessage(LOCAL, R.string.status_searching_articles)
        }

    @Test
    fun observableArticles() = runBlockingTest {
        assertThat(viewModel.articleLists.getValueOrAwaitFlowValue()!!.size, greaterThan(0))
    }

    private suspend fun assertStatusMessage(source: Source, @StringRes message: Int) {
        viewModel.searchQuery.offer("search query")
        viewModel.dataSource.offer(source)
        viewModel.articleLists.getValueOrAwaitFlowValue()
        assertThat(
            viewModel.statusMessage.getOrAwaitValue(),
            equalToIgnoringCase(viewModel.context.getString(message))
        )
    }

    companion object {
        private val REMOTE_ARTICLES = createArticles(2)
    }
}