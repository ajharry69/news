package com.xently.tests.ui.rules

import android.app.Application
import androidx.annotation.NavigationRes
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestWatcher
import org.junit.runner.Description


class NavControllerRule(@NavigationRes private val navigationResId: Int) : TestWatcher() {
    lateinit var navController: TestNavHostController
    override fun starting(description: Description?) {
        super.starting(description)

        val application = ApplicationProvider.getApplicationContext() as Application
        navController = TestNavHostController(application.applicationContext).apply {
            setGraph(navigationResId)
            enableOnBackPressed(true)
        }
    }
}