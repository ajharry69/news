package com.xently.news.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.xently.news.databinding.ArticleListFragmentBinding
import com.xently.utilities.ui.fragments.ListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleListFragment : ListFragment() {

    private val viewModel: ArticleListViewModel by viewModels()

    private var _binding: ArticleListFragmentBinding? = null
    private val binding: ArticleListFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ArticleListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}