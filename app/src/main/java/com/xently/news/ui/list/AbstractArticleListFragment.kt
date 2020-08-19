package com.xently.news.ui.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.xently.news.R
import com.xently.models.Article
import com.xently.news.databinding.ArticleListFragmentBinding
import com.xently.news.ui.list.utils.ArticlesAdapter
import com.xently.news.ui.utils.startShareArticleIntent
import com.xently.utilities.ui.fragments.ListFragment
import com.xently.utilities.viewext.showSnackBar

abstract class AbstractArticleListFragment : ListFragment(),
    ArticlesAdapter.OnActionButtonClickListener {
    private lateinit var articlesAdapter: ArticlesAdapter
    abstract val viewModel: AbstractArticleListViewModel

    private var _binding: ArticleListFragmentBinding? = null
    protected val binding: ArticleListFragmentBinding
        get() = _binding!!

    override fun onActionButtonClick(article: Article, view: View) {
        when (view.id) {
            R.id.share -> startShareArticleIntent(view.context, article)
            R.id.add_bookmark -> viewModel.addBookmark(article.id, !article.bookmarked)
            R.id.add_comment -> showSnackBar(
                "TODO: navigate to comments screen...",
                Snackbar.LENGTH_LONG
            ) // TODO: navigate to comments screen...
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articlesAdapter = ArticlesAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ArticleListFragmentBinding.inflate(inflater, container, false).apply {
            (activity as? AppCompatActivity)?.run {
                setSupportActionBar(toolbar)
            }
            setupToolbar(toolbar)
            filtered = false
            articles.adapter = articlesAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.startArticleListRefresh.offer(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.run {
            viewModel = this@AbstractArticleListFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        viewModel.articleLists.observe(viewLifecycleOwner, {
            articlesAdapter.submitList(it)
        })
        viewModel.showStatusView.observe(viewLifecycleOwner, {
            binding.run {
                swipeRefresh.isVisible = !it
                statusContainer.isVisible = it
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_articles -> activity?.onSearchRequested() ?: false
            R.id.refresh -> {
                viewModel.startArticleListRefresh.offer(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateSearchIdentifier() = SEARCH_IDENTIFIER

    companion object {
        val SEARCH_IDENTIFIER: String = AbstractArticleListFragment::class.java.name
    }
}