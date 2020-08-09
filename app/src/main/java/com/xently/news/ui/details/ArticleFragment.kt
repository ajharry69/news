package com.xently.news.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.xently.news.databinding.ArticleFragmentBinding
import com.xently.utilities.ui.fragments.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private val viewModel: ArticleViewModel by viewModels()

    private var _binding: ArticleFragmentBinding? = null
    private val binding: ArticleFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ArticleFragmentBinding.inflate(inflater, container, false)
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