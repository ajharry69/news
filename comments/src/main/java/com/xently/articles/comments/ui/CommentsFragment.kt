package com.xently.articles.comments.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.xently.articles.comments.R
import com.xently.articles.comments.databinding.CommentsFragmentBinding
import com.xently.utilities.ui.fragments.ListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : ListFragment() {
    private val args: CommentsFragmentArgs by navArgs()
    private val viewModel: CommentsViewModel by viewModels()
    private var _binding: CommentsFragmentBinding? = null
    private val binding: CommentsFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CommentsFragmentBinding.inflate(inflater, container, false).apply {
            (activity as? AppCompatActivity)?.run {
                setSupportActionBar(toolbar)
            }
            setupToolbar(toolbar)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.run {
            articleId.offer(args.articleId)
            binding.run {
                viewModel = this@CommentsFragment.viewModel
                lifecycleOwner = viewLifecycleOwner
                article = args.article
            }
            commentListResults.observe(viewLifecycleOwner, {

            })
            showStatusView.observe(viewLifecycleOwner, {

            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.comments_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_articles -> activity?.onSearchRequested() ?: false
            R.id.refresh -> {
                viewModel.startCommentListRefresh.offer(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}