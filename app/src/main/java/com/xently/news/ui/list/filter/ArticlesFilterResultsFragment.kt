package com.xently.news.ui.list.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.xently.news.ui.list.AbstractArticleListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticlesFilterResultsFragment : AbstractArticleListFragment() {

    override val viewModel: ArticlesFilterResultsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.filtered = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}