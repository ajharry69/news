package com.xently.news.data.source.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.xently.news.ARTICLE
import com.xently.news.createArticles
import com.xently.news.data.model.Article
import com.xently.tests.unit.rules.MainCoroutineRule
import com.xently.tests.unit.rules.RoomDatabaseRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@Ignore("Unable to test Room DAOs when FTS entities(tables) are present in DB")
class ArticleDAOTest {

    @get:Rule
    val databaseRule = RoomDatabaseRule(NewsDatabase::class.java)

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dao: ArticleDAO

    @Before
    fun initDAO() {
        dao = databaseRule.database.articlesDAO
    }

    @Test
    fun saveArticles() = runBlockingTest {
        val articles = createArticles(2).toTypedArray()
        // save 2 articles(created above)
        assertThat(dao.saveArticles(*articles).size, equalTo(2))

        // get articles contains the 2 articles saved above
        assertThat(dao.getArticles().map { it.article }, contains(*articles))

        // get observable articles contains the 2 (saved) articles
        dao.getObservableArticles().test {
            assertThat(expectItem().map { it.article }, contains(*articles))
            cancelAndIgnoreRemainingEvents()
        }

        // get single (saved) article by their id returns an article
        assertThat(dao.getArticle(articles[0].id)?.article, notNullValue(Article::class.java))

        // get single (saved) observable article by their id returns an article
        dao.getObservableArticle(articles[0].id).test {
            assertThat(expectItem().article, notNullValue(Article::class.java))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addBookmark() = runBlockingTest {
        dao.saveArticles(ARTICLE.copy(bookmarked = false))

        // add bookmark to saved article
        assertThat(dao.addBookMark(ARTICLE.id, true), equalTo(1))

        val article = dao.getArticle(ARTICLE.id)?.article
        assertThat(article, notNullValue(Article::class.java))
        assertThat(article!!.bookmarked, `is`(true))
    }

    @Test
    fun `get observable or regular Articles from a search query(FTS)`() = runBlockingTest {
        dao.saveArticles(
            *createArticles(10).toTypedArray(),
            ARTICLE.copy(id = Long.MAX_VALUE, headline = "Very very unique article headline")
        )

        assertThat(dao.getArticles("very very").size, equalTo(1))
        dao.getObservableArticles("very very").test {
            assertThat(expectItem().size, equalTo(1))
            cancelAndIgnoreRemainingEvents()
        }
    }
}