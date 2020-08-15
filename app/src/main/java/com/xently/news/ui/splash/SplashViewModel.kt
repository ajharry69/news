package com.xently.news.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.xently.common.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.delay
import kotlin.random.Random

class SplashViewModel @ViewModelInject constructor() : ViewModel() {
    fun showArticleListFragment(test: Boolean = false) = wrapEspressoIdlingResource {
        liveData {
            if (!test) delay(Random.nextLong(1, 500))
            emit(true)
        }
    }
}