package com.xently.news.ui.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.xently.articles.comments.ui.CommentsFragmentArgs
import com.xently.models.Article
import com.xently.news.R
import com.xently.news.databinding.ArticleListFragmentBinding
import com.xently.news.ui.list.utils.ArticlesAdapter
import com.xently.news.ui.list.utils.OnActionButtonClickListener
import com.xently.news.ui.utils.startShareArticleIntent
import com.xently.utilities.ui.fragments.ListFragment

abstract class AbstractArticleListFragment : ListFragment(), OnActionButtonClickListener {
    private lateinit var articlesAdapter: ArticlesAdapter
    abstract val viewModel: AbstractArticleListViewModel

    private var _binding: ArticleListFragmentBinding? = null
    protected val binding: ArticleListFragmentBinding
        get() = _binding!!

    override fun onActionButtonClick(article: Article, view: View) {
        when (view.id) {
            R.id.share -> startShareArticleIntent(view.context, article)
            R.id.add_bookmark -> viewModel.addBookmark(article.id, !article.bookmarked)
            R.id.add_comment -> {
                view.findNavController().navigate(
                    R.id.nav_graph_comments,
                    CommentsFragmentArgs(article.id, article).toBundle(),
                    navOptions {
                        launchSingleTop = true
                    },
                )
            }
            R.id.flag_inappropriate -> viewModel.flagArticle(article.id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articlesAdapter = ArticlesAdapter(this)
//            .apply { withLoadStateFooter(LoadStateAdapter(this)) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
            startRefresh()
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
        /*lifecycleScope.launch {
            articlesAdapter.loadStateFlow.collectLatest {
                viewModel.startArticleListRefresh.send(it.refresh is LoadState.Loading)
            }
        }
        lifecycleScope.launch {
            viewModel.getObservableArticles().collectLatest {
                articlesAdapter.submitData(it)
            }
        }*/
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
                startRefresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateSearchIdentifier() = SEARCH_IDENTIFIER

    private fun startRefresh(adapter: Adapter<*> = articlesAdapter) {
        if (adapter is PagingDataAdapter<*, *>) adapter.refresh() else {
            viewModel.startArticleListRefresh.offer(true)
        }
    }

    companion object {
        val SEARCH_IDENTIFIER: String = AbstractArticleListFragment::class.java.name
    }
}