package com.xently.news.ui.list

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xently.news.R
import com.xently.models.Article
import com.xently.news.data.repository.ArticlesRepository
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.fakes.FakeArticleDataSource
import com.xently.tests.unit.getOrAwaitValue
import com.xently.tests.unit.getValueOrAwaitFlowValue
import com.xently.tests.unit.rules.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AbstractArticleListViewModelImplTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var repository: IArticlesRepository
    private lateinit var viewModel: AbstractArticleListViewModelImpl

    @Before
    fun setUp() {
        viewModel = createViewModel(createRepository())
    }

    @After
    fun tearDown() {
        repository = createRepository()
        // reset to avoid affecting other tests
        viewModel.startArticleListRefresh.offer(false)
    }

    @Test
    fun defaultObservablesStates() {
        val showHorizontalProgress = viewModel.showHorizontalProgressbar.getOrAwaitValue()
        val showProgress = viewModel.showProgressbar.getOrAwaitValue()
        val showSwipeRefreshProgressIndicator =
            viewModel.showSwipeRefreshProgressIndicator.getOrAwaitValue()
        val statusMessage = viewModel.statusMessage.getOrAwaitValue()

        assertThat(showHorizontalProgress, equalTo(false))
        assertThat(showProgress, equalTo(false))
        assertThat(showSwipeRefreshProgressIndicator, equalTo(false))
        assertThat(statusMessage, equalTo(context.getString(R.string.status_articles_empty)))
    }

    @Test
    fun `startArticleListRefresh value matches showSwipeRefreshProgressIndicator value`() {
        viewModel.run {
            startArticleListRefresh.offer(false)
            assertThat(showSwipeRefreshProgressIndicator.getOrAwaitValue(), equalTo(false))
            startArticleListRefresh.offer(true)
            assertThat(showSwipeRefreshProgressIndicator.getOrAwaitValue(), equalTo(true))
        }
    }

    /**
     * showHorizontalProgressbar value influences the value of showProgressbar
     */
    @Test
    fun `setting value of true to both showProgressbar and showHorizontalProgressbar`() {
        viewModel.run {
            setShowProgressbar(true)
            setShowHorizontalProgressbar(true)

            assertThat(showProgressbar.getOrAwaitValue(), equalTo(false))
            assertThat(showHorizontalProgressbar.getOrAwaitValue(), equalTo(true))
        }
    }

    /**
     * showSwipeRefreshProgressIndicator value influences the value of showSwipeRefreshProgressIndicator
     */
    @Test
    fun `setting value of true to both showHorizontalProgressbar and showSwipeRefreshProgressIndicator`() {
        viewModel.run {
            setShowHorizontalProgressbar(true)
            setShowSwipeRefreshProgressIndicator(true)

            assertThat(showHorizontalProgressbar.getOrAwaitValue(), equalTo(false))
            assertThat(showSwipeRefreshProgressIndicator.getOrAwaitValue(), equalTo(true))
        }
    }

    /**
     * showSwipeRefreshProgressIndicator value influences the value of showSwipeRefreshProgressIndicator
     */
    @Test
    fun `setting value of true to both showHorizontalProgressbar and startArticleListRefresh`() {
        viewModel.run {
            setShowHorizontalProgressbar(true)
            startArticleListRefresh.offer(true)

            assertThat(showHorizontalProgressbar.getOrAwaitValue(), equalTo(false))
            assertThat(showSwipeRefreshProgressIndicator.getOrAwaitValue(), equalTo(true))
        }
    }

    @Test
    fun `setting value of true to showProgressbar, showHorizontalProgressbar and startArticleListRefresh`() {
        viewModel.run {
            setShowProgressbar(true)
            setShowHorizontalProgressbar(true)
            startArticleListRefresh.offer(true)

            assertThat(showProgressbar.getOrAwaitValue(), equalTo(false))
            assertThat(showHorizontalProgressbar.getOrAwaitValue(), equalTo(false))
            assertThat(showSwipeRefreshProgressIndicator.getOrAwaitValue(), equalTo(true))
        }
    }

    @Test
    fun `false as value for showHorizontalProgressbar does not change previous value of showProgressbar`() {
        viewModel.run {
            setShowProgressbar(true)
            setShowHorizontalProgressbar(false)

            assertThat(showProgressbar.getOrAwaitValue(), equalTo(true))
            assertThat(showHorizontalProgressbar.getOrAwaitValue(), equalTo(false))
        }
    }

    @Test
    fun `false as value for showSwipeRefreshProgressIndicator does not change previous value of showHorizontalProgressbar`() {
        viewModel.run {
            setShowHorizontalProgressbar(true)
            setShowSwipeRefreshProgressIndicator(false)

            assertThat(showHorizontalProgressbar.getOrAwaitValue(), equalTo(true))
            assertThat(showSwipeRefreshProgressIndicator.getOrAwaitValue(), equalTo(false))
        }
    }

    @Test
    fun `false as value for startArticleListRefresh does not change previous value of showHorizontalProgressbar`() {
        viewModel.run {
            setShowHorizontalProgressbar(true)
            startArticleListRefresh.offer(false)

            assertThat(showHorizontalProgressbar.getOrAwaitValue(), equalTo(true))
            assertThat(showSwipeRefreshProgressIndicator.getOrAwaitValue(), equalTo(false))
        }
    }

    @Test
    fun showStatusView() = runBlockingTest {
        viewModel.run {
            setShowProgressbar(false)
            setStatusMessage(null)
            assertThat(showStatusView.getValueOrAwaitFlowValue(), equalTo(false))
            assertThat(showProgressbar.getOrAwaitValue(), equalTo(false))

            setShowProgressbar(false)
            setStatusMessage("") // blank messages not allowed
            assertThat(showStatusView.getValueOrAwaitFlowValue(), equalTo(false))
            assertThat(showProgressbar.getOrAwaitValue(), equalTo(false))

            setShowProgressbar(false)
            setStatusMessage("      ") // blank messages not allowed
            assertThat(showStatusView.getValueOrAwaitFlowValue(), equalTo(false))
            assertThat(showProgressbar.getOrAwaitValue(), equalTo(false))

            setShowProgressbar(true)
            setStatusMessage(null)
            assertThat(showStatusView.getValueOrAwaitFlowValue(), equalTo(true))
            assertThat(showProgressbar.getOrAwaitValue(), equalTo(true))

            setShowProgressbar(false)
            setStatusMessage(R.string.status_loading) // message was shown
            assertThat(showStatusView.getValueOrAwaitFlowValue(), equalTo(true))
            assertThat(showProgressbar.getOrAwaitValue(), equalTo(false))

            setShowProgressbar(true)
            setStatusMessage(R.string.status_loading)
            assertThat(showStatusView.getValueOrAwaitFlowValue(), equalTo(true))
            assertThat(showProgressbar.getOrAwaitValue(), equalTo(true))
        }
    }

    private fun createRepository(
        localData: Array<Article> = emptyArray(),
        remoteData: Array<Article> = emptyArray()
    ): IArticlesRepository {
        val local = FakeArticleDataSource(*localData)
        val remote = FakeArticleDataSource(*remoteData)
        return ArticlesRepository(local, remote, Dispatchers.Unconfined)
    }

    private fun createViewModel(repository: IArticlesRepository): AbstractArticleListViewModelImpl {
        createRepository()
        val app = ApplicationProvider.getApplicationContext() as Application
        context = app.applicationContext
        return AbstractArticleListViewModelImpl(app, repository)
    }
}