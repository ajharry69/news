package com.xently.news.ui.details

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.xently.news.ARTICLE
import com.xently.news.HiltTestActivity
import com.xently.news.R
import com.xently.news.di.modules.DataSourceModule
import com.xently.tests.ui.launchFragmentInHiltContainer
import com.xently.tests.ui.rules.IdlingResourceRule
import com.xently.tests.ui.rules.NavControllerRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
class ArticleFragmentTest {

    @get:Rule
    val androidRule = HiltAndroidRule(this)

    @get:Rule
    val idlingResourceRule = IdlingResourceRule()

    @get:Rule
    val navControllerRule = NavControllerRule(R.navigation.nav_graph)

    private lateinit var navController: TestNavHostController

    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    @Before
    fun setUp() {
        androidRule.inject()
        navController = navControllerRule.navController
        val bundle: Bundle = ArticleFragmentArgs(ARTICLE.id).toBundle()
        scenario = launchFragmentInHiltContainer<HiltTestActivity, ArticleFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            idlingResourceRule.dataBindingIdlingResource.monitorFragment(this)
        }
    }

    @Test
    fun viewsAreVisible() {
        onView(withId(R.id.author_photo)).check(matches(isDisplayed()))
        onView(withId(R.id.headline)).check(matches(isDisplayed()))
        onView(withId(R.id.sub_headline)).check(matches(isDisplayed()))
        onView(withId(R.id.content)).check(matches(isDisplayed()))
    }
}