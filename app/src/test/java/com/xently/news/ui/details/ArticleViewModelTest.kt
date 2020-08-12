package com.xently.news.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.xently.common.data.Source
import com.xently.common.data.TaskResult.Loading
import com.xently.news.ARTICLE
import com.xently.news.createArticles
import com.xently.news.data.repository.ArticlesRepository
import com.xently.news.data.repository.IArticlesRepository
import com.xently.news.fakes.FakeArticleDataSource
import com.xently.tests.unit.getOrAwaitValue
import com.xently.tests.unit.rules.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
        viewModel = ArticleViewModel(repository)
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
    fun `getArticle initializes article when an article is successfully retrieved`() {
        viewModel.getArticle(REMOTE_ARTICLES[0].id)

        assertThat(viewModel.article.getOrAwaitValue(), equalTo(REMOTE_ARTICLES[0]))
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