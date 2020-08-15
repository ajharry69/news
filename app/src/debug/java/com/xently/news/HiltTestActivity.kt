package com.xently.news

import android.os.Bundle
import com.xently.utilities.ui.SearchableActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity : SearchableActivity() {
    override fun onSearchIntentReceived(query: String, searchData: Bundle?) {

    }
}