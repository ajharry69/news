package com.xently.news.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xently.news.databinding.SplashFragmentBinding
import com.xently.news.ui.splash.SplashFragmentDirections.Companion.actionDestSplashToDestArticleList
import com.xently.utilities.ui.fragments.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    private var _binding: SplashFragmentBinding? = null
    private val binding: SplashFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.showArticleListFragment().observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(actionDestSplashToDestArticleList())
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}