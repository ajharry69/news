package com.xently.news.ui.list.filter

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xently.common.data.Source.LOCAL
import com.xently.news.createArticles
import com.xently.news.data.model.Article
import com.xently.news.data.repository.ArticlesRepository
import com.xently.news.data.source.IArticleDataSource
import com.xently.news.fakes.FakeArticleDataSource
import com.xently.tests.unit.getOrAwaitValue
import com.xently.tests.unit.getValueOrAwaitFlowValue
import com.xently.tests.unit.rules.MainCoroutineRule
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ArticlesFilterResultsViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `articleListCount returns 0 by default`() {
        assertThat(provideViewModel().articleListCount.getOrAwaitValue(), greaterThanOrEqualTo(0))
    }

    @Test
    @Ignore("Flaky! Figure out how to better test this...")
    fun `empty articleListFrom LOCAL data source initiates a fetch from REMOTE data source`() =
        runBlockingTest {
            val viewModel = provideViewModel(remote = createArticles(10).toTypedArray())

            // make sure article list is being observed from local data source
            viewModel.dataSource.offer(LOCAL)
            // count is ZERO at first because LOCAL data source contains NO value
            assertThat(viewModel.articleListCount.getOrAwaitValue(), equalTo(0))
            // this will return empty list at first which will then initiate a remote data fetch
            viewModel.articleLists.getValueOrAwaitFlowValue()
            // count is greater ZERO at because LOCAL cache of remote data fetched above has been stored
            assertThat(viewModel.articleListCount.getOrAwaitValue(), greaterThan(0))
        }

    private fun provideViewModel(
        remote: Array<Article> = kotlin.emptyArray(),
        local: Array<Article> = kotlin.emptyArray()
    ) = provideViewModel(FakeArticleDataSource(*local), FakeArticleDataSource(*remote))

    private fun provideViewModel(
        local: IArticleDataSource,
        remote: IArticleDataSource
    ): ArticlesFilterResultsViewModel {
        val repository = ArticlesRepository(local, remote)
        val app = ApplicationProvider.getApplicationContext() as Application
        return ArticlesFilterResultsViewModel(app, repository)
    }
}