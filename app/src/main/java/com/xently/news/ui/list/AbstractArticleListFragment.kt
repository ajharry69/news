package com.xently.news.ui.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.xently.news.R
import com.xently.news.databinding.ArticleListFragmentBinding
import com.xently.news.ui.list.utils.ArticlesAdapter
import com.xently.utilities.ui.fragments.ListFragment
import javax.inject.Inject

abstract class AbstractArticleListFragment : ListFragment() {
    @Inject
    lateinit var articlesAdapter: ArticlesAdapter
    abstract val viewModel: AbstractArticleListViewModel

    private var _binding: ArticleListFragmentBinding? = null
    protected val binding: ArticleListFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ArticleListFragmentBinding.inflate(inflater, container, false).apply {
            (activity as? AppCompatActivity)?.run {
                setSupportActionBar(toolbar)
            }
            setupToolbar(toolbar)
            viewModel = this@AbstractArticleListFragment.viewModel
            lifecycleOwner = this@AbstractArticleListFragment
            filtered = false
            articles.adapter = articlesAdapter
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.articleLists.observe(viewLifecycleOwner, Observer {
            articlesAdapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.search_articles) {
            activity?.onSearchRequested() ?: false
        } else super.onOptionsItemSelected(item)
    }
}