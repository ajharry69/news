package com.xently.articles.comments.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.xently.articles.comments.databinding.CommentsFragmentBinding
import com.xently.utilities.ui.fragments.ListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : ListFragment() {
    private val viewModel: CommentsViewModel by viewModels()
    private var _binding: CommentsFragmentBinding? = null
    private val binding: CommentsFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CommentsFragmentBinding.inflate(inflater, container, false).apply {

        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}