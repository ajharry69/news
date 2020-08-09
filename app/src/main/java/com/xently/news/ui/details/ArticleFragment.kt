package com.xently.news.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.xently.common.data.TaskResult
import com.xently.news.databinding.ArticleFragmentBinding
import com.xently.utilities.ui.fragments.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private val args: ArticleFragmentArgs by navArgs()
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
        viewModel.articleFetchResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is TaskResult.Success -> {

                }
                is TaskResult.Error -> {

                }
                TaskResult -> {

                }
            }
        })
        viewModel.getObservableArticle(args.articleId).observe(viewLifecycleOwner, Observer {

        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}