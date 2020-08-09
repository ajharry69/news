package com.xently.news.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.delay
import kotlin.random.Random

class SplashViewModel @ViewModelInject constructor() : ViewModel() {
    fun showArticleListFragment(): LiveData<Boolean> {
        return liveData {
            delay(Random.nextLong(1, 1000))
            emit(true)
        }
    }
}