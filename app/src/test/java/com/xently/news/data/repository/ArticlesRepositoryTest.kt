package com.xently.news.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.xently.common.data.Source.LOCAL
import com.xently.common.data.Source.REMOTE
import com.xently.common.data.TaskResult
import com.xently.common.data.data
import com.xently.news.ARTICLE
import com.xently.news.createArticles
import com.xently.news.data.model.Article
import com.xently.news.data.source.IArticleDataSource
import com.xently.news.fakes.FakeArticleDataSource
import com.xently.tests.unit.assertDataEqual
import com.xently.tests.unit.assertDataNotNullValue
import com.xently.tests.unit.getValueOrWait
import com.xently.tests.unit.rules.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.random.Random

class ArticlesRepositoryTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var local: IArticleDataSource
    private lateinit var remote: IArticleDataSource
    private lateinit var repository: ArticlesRepository

    @Before
    fun setUp() {
        local = FakeArticleDataSource()
        remote = FakeArticleDataSource(*REMOTE_ARTICLES.toTypedArray())
        repository = ArticlesRepository(local, remote, Dispatchers.Unconfined)
    }

    @Test
    fun `saveArticles saves the records remotely and a cache locally`() = runBlockingTest {
        val results =
            repository.saveArticles(ARTICLE.copy(id = Long.MAX_VALUE, headline = "Headline"))

        results.assertDataNotNullValue()
        val articlesCount = results.data!!.size
        // only one article was saved
        assertThat(articlesCount, equalTo(1))
        // local cache is stored too
        assertThat(local.getArticles().data!!.size, greaterThanOrEqualTo(articlesCount))
        // articles are saved remotely
        assertThat(remote.getArticles().data!!.size, greaterThanOrEqualTo(articlesCount))
    }

    @Test
    fun `getArticles with a null, empty or blank searchQuery returns Success`() = runBlockingTest {
        val block: (TaskResult<List<Article>>).() -> Unit = {
            assertDataNotNullValue()
            assertThat(data!!.size, greaterThanOrEqualTo(REMOTE_ARTICLES.size))
        }
        // null search query
        repository.getArticles(null).run(block)
        // blank search query
        repository.getArticles("").run(block)
    }

    @Test
    fun `getObservableArticles from REMOTE source with a null, empty or blank searchQuery returns collection of Articles`() =
        runBlockingTest {
            val validate: suspend FlowTurbine<List<Article>>.() -> Unit = {
                assertThat(expectItem().size, greaterThanOrEqualTo(REMOTE_ARTICLES.size))
                cancelAndIgnoreRemainingEvents()
            }
            repository.getObservableArticles(null, REMOTE).test(validate = validate)
            repository.getObservableArticles("", REMOTE).test(validate = validate)
        }

    @Test
    fun `getObservableArticles from LOCAL source with a null, empty or blank searchQuery returns collection of Articles`() =
        runBlockingTest {
            repository.saveArticles(ARTICLE)
            val validate: suspend FlowTurbine<List<Article>>.() -> Unit = {
                assertThat(expectItem().size, greaterThanOrEqualTo(1))
                cancelAndIgnoreRemainingEvents()
            }
            repository.getObservableArticles(null, LOCAL).test(validate = validate)
            repository.getObservableArticles("", LOCAL).test(validate = validate)
        }

    @Test
    fun `getArticles with a none-null or blank searchQuery returns Success with filtered search results`() =
        runBlockingTest {
            val articleSearched = ARTICLE.copy(headline = "Unique headline!")
            repository.saveArticles(articleSearched)
            val block: (TaskResult<List<Article>>).() -> Unit = {
                assertDataNotNullValue()
                assertThat(data, contains(articleSearched))
            }
            repository.getArticles("Unique").run(block)
            // search is case insensitive
            repository.getArticles("unIqUe").run(block)
            // unmatched item
            repository.getArticles("random unknown article content").run {
                assertDataNotNullValue()
                assertThat(data!!.size, equalTo(0))
            }
        }

    @Test
    fun `getObservableArticles from REMOTE source with a none-null or blank searchQuery returns collection of Articles`() =
        runBlockingTest {
            val articleSearched = ARTICLE.copy(id = 321, headline = "Uniqu321e headline!")
            repository.saveArticles(articleSearched)
            val validate: suspend FlowTurbine<List<Article>>.() -> Unit = {
                assertThat(expectItem(), contains(articleSearched))
                cancelAndIgnoreRemainingEvents()
            }
            repository.getObservableArticles("Uniqu321e", REMOTE).test(validate = validate)
            // search is case insensitive
            repository.getObservableArticles("unIqU321e", REMOTE).test(validate = validate)
            // unmatched item
            repository.getObservableArticles("random unknown article content", REMOTE).test {
                assertThat(expectItem().size, equalTo(0))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getObservableArticles from LOCAL source with a none-null or blank searchQuery returns collection of Articles`() =
        runBlockingTest {
            val articleSearched = ARTICLE.copy(id = 123, headline = "Uniqu123 headline!")
            repository.saveArticles(articleSearched)
            val validate: suspend FlowTurbine<List<Article>>.() -> Unit = {
                assertThat(expectItem(), contains(articleSearched))
                cancelAndIgnoreRemainingEvents()
            }
            repository.run {
                getObservableArticles("Uniqu123", LOCAL).run {
                    assertThat(getValueOrWait(), contains(articleSearched))
                    test(validate = validate)
                }
                // search is case insensitive
                getObservableArticles("unIqU123", LOCAL).run {
                    assertThat(getValueOrWait(), contains(articleSearched))
                    test(validate = validate)
                }
                // unmatched item
                getObservableArticles("random unknown article content", LOCAL).test {
                    assertThat(expectItem().size, equalTo(0))
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

    @Test
    fun getArticle() = runBlockingTest {
        val article = ARTICLE.copy(id = Random.nextLong(999, 1_999_999))
        repository.saveArticles(article)

        // unknown id
        repository.getArticle(-1).run {
            assertThat(this, instanceOf(TaskResult.Error::class.java))
            assertThat(data, nullValue(Article::class.java))
        }

        // known id
        repository.getArticle(article.id).run {
            assertDataEqual(article)
        }
    }

    @Test
    fun `getObservableArticle from REMOTE data source`() = runBlockingTest {
        val article = ARTICLE.copy(id = Random.nextLong(900, 1_000))
        repository.saveArticles(article)

        // unknown id
        repository.getObservableArticle(-1, REMOTE).test {
            assertThat(expectError().message?.toLowerCase(Locale.ROOT), containsString("not found"))
        }

        // known id
        repository.getObservableArticle(article.id, REMOTE).test {
            assertThat(expectItem(), equalTo(article))
        }
    }

    @Test
    fun `getObservableArticle from LOCAL data source`() = runBlockingTest {
        val article = ARTICLE.copy(id = Random.nextLong(1_001, 10_000))
        repository.saveArticles(article)

        // unknown id
        repository.getObservableArticle(-1, LOCAL).test {
            assertThat(expectError().message?.toLowerCase(Locale.ROOT), containsString("not found"))
        }

        // known id
        repository.getObservableArticle(article.id, LOCAL).test {
            assertThat(expectItem(), equalTo(article))
        }
    }

    @Test
    fun addBookMark() = runBlockingTest {
        val article = ARTICLE.copy(id = Random.nextLong(10_001, 100_000), bookmarked = false)
        repository.saveArticles(article)

        // before changing bookmark status
        repository.getObservableArticle(article.id).test {
            assertThat(expectItem().bookmarked, equalTo(false))
        }
        repository.addBookMark(article.id, true)
        // after changing bookmark status
        repository.getObservableArticle(article.id).test {
            assertThat(expectItem().bookmarked, equalTo(true))
        }

    }

    companion object {
        private val REMOTE_ARTICLES = createArticles(2)
    }
}