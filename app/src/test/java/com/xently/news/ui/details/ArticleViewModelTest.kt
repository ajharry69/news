package com.xently.news.ui.details

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.TaskResult.Loading
import com.xently.news.ARTICLE
import com.xently.news.createArticles
import com.xently.news.data.model.Article
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
class ArticleViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: IArticlesRepository
    private lateinit var viewModel: ArticleViewModel

    @Before
    fun setUp() {
        val local = FakeArticleDataSource(ARTICLE)
        val remote = FakeArticleDataSource(*REMOTE_ARTICLES.toTypedArray(), ARTICLE)
        repository = ArticlesRepository(local, remote, Dispatchers.Unconfined)
        viewModel =
            ArticleViewModel(ApplicationProvider.getApplicationContext() as Application, repository)
    }

    @Test
    fun showProgressbar() {
        // show progress bar is false by default
        assertThat(viewModel.showProgressbar.getOrAwaitValue(), `is`(false))

        // add bookmark hides/shows progress bar
        mainCoroutineRule.pauseDispatcher()
        viewModel.addBookmark(ARTICLE.id, !ARTICLE.bookmarked)
        assertProgressbarVisibility()

        // getArticle hides/shows progress bar
        mainCoroutineRule.pauseDispatcher()
        viewModel.getArticle(ARTICLE.id)
        assertProgressbarVisibility()

        // getObservableArticle with un-cached article id hides/shows progress bar
        /*mainCoroutineRule.pauseDispatcher()
        viewModel.getObservableArticle(1234, Source.LOCAL).getOrAwaitValue()
        assertProgressbarVisibility()*/
    }

    @Test
    fun `addBookmark immediately returns Loading TaskResult then reverts to Success or Error upon addBookmark completion`() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.addBookmark(ARTICLE.id, !ARTICLE.bookmarked)

        assertThat(viewModel.addBookmarkResult.getOrAwaitValue(), instanceOf(Loading::class.java))

        mainCoroutineRule.resumeDispatcher()
        assertThat(
            viewModel.addBookmarkResult.getOrAwaitValue(),
            not(instanceOf(Loading::class.java))
        )
    }

    @Test
    fun `getArticle immediately returns Loading TaskResult then reverts to Success or Error upon addBookmark completion`() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.getArticle(ARTICLE.id)

        assertThat(viewModel.articleFetchResult.getOrAwaitValue(), instanceOf(Loading::class.java))

        mainCoroutineRule.resumeDispatcher()
        assertThat(
            viewModel.articleFetchResult.getOrAwaitValue(),
            not(instanceOf(Loading::class.java))
        )
    }

    @Test
    fun `observing article from (REMOTE or LOCAL) source with an existing id returns expected Article`() =
        runBlockingTest {
            viewModel.run {
                val id = REMOTE_ARTICLES[0].id
                dataSource.offer(REMOTE)
                articleId.offer(id)
                assertThat(article.getValueOrAwaitFlowValue(), equalTo(REMOTE_ARTICLES[0]))

                getArticle(id) // will force a LOCAL data cache
                dataSource.offer(LOCAL)
                assertThat(article.getValueOrAwaitFlowValue(), equalTo(REMOTE_ARTICLES[0]))
            }
        }

    @Test
    fun `observing article from (REMOTE or LOCAL) source with an un-existing id returns null`() =
        runBlockingTest {
            viewModel.run {
                val id = -123L // does not exist locally or remotely
                dataSource.offer(REMOTE)
                articleId.offer(id)
                assertThat(article.getValueOrAwaitFlowValue(), nullValue(Article::class.java))

                dataSource.offer(LOCAL)
                assertThat(article.getValueOrAwaitFlowValue(), nullValue(Article::class.java))
            }
        }

    private fun assertProgressbarVisibility() {
        assertThat(viewModel.showProgressbar.getOrAwaitValue(), equalTo(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showProgressbar.getOrAwaitValue(), equalTo(false))
    }

    companion object {
        private val REMOTE_ARTICLES = createArticles(2)
    }
}