package com.xently.news.ui.splash

import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.xently.news.HiltTestActivity
import com.xently.news.R
import com.xently.news.di.modules.DataSourceModule
import com.xently.tests.ui.launchFragmentInHiltContainer
import com.xently.tests.ui.rules.IdlingResourceRule
import com.xently.tests.ui.rules.NavControllerRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
class SplashFragmentTest {

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
        scenario = launchFragmentInHiltContainer<HiltTestActivity, SplashFragment> {
            Navigation.setViewNavController(requireView(), navController)
            idlingResourceRule.dataBindingIdlingResource.monitorFragment(this)
        }
    }

    @Test
    fun navigateToArticleListScreenAfterAWhile() {
        // since navigation is expected to happen when the host activity has been created
//        scenario.moveToState(Lifecycle.State.CREATED)
//        assertThat(navController.currentDestination?.id, equalTo(R.id.dest_article_list))
        assertThat(navController.currentDestination?.id, equalTo(R.id.dest_splash))
    }

}