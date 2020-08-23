package com.xently.news.ui.details

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.xently.articles.comments.ui.CommentsFragmentArgs
import com.xently.common.data.TaskResult
import com.xently.common.data.errorMessage
import com.xently.media.ui.MediaFragment
import com.xently.models.Article
import com.xently.news.BR
import com.xently.news.R
import com.xently.news.databinding.ArticleFragmentBinding
import com.xently.news.ui.utils.setChips
import com.xently.news.ui.utils.startShareArticleIntent
import com.xently.utilities.ui.fragments.Fragment
import com.xently.utilities.viewext.getThemedColor
import com.xently.utilities.viewext.showSnackBar
import com.xently.utilities.viewext.tintDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private lateinit var article: Article
    private val args: ArticleFragmentArgs by navArgs()
    private val viewModel: ArticleViewModel by viewModels()

    private var _binding: ArticleFragmentBinding? = null
    private val binding: ArticleFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ArticleFragmentBinding.inflate(inflater, container, false).apply {
            setupToolbar(toolbar)
            lifecycleOwner = this@ArticleFragment
            viewModel = this@ArticleFragment.viewModel
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val taskResultObserver = Observer<TaskResult<Any>> {
            if (it is TaskResult.Error) it.errorMessage?.let { message ->
                showSnackBar(message, Snackbar.LENGTH_LONG)
            }
        }
        viewModel.run {
            viewModel.articleId.offer(args.articleId)
            addBookmarkResult.observe(viewLifecycleOwner, taskResultObserver)
            flagArticleResult.observe(viewLifecycleOwner, taskResultObserver)
            articleFetchResult.observe(viewLifecycleOwner, taskResultObserver)
            article.observe(viewLifecycleOwner, {
                this@ArticleFragment.article = it
                binding.run {
                    setVariable(BR.article, it)
                    executePendingBindings()
                    details.tags.isVisible = it.chipDataList.isNotEmpty()
                    setChips(details.tags, it.chipDataList)
                    updateBookmarkMenuItem(toolbar.menu.findItem(R.id.add_bookmark), it.bookmarked)
                }
                (childFragmentManager.findFragmentById(R.id.media) as? MediaFragment)?.run {
                    setMediaUris(*it.mediaUris.toTypedArray())
                }
            })
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_bookmark -> {
            viewModel.addBookmark(args.articleId, !article.bookmarked)
            true
        }
        R.id.flag_inappropriate -> {
            viewModel.flagArticle(args.articleId)
            true
        }
        R.id.add_comment -> {
            findNavController().navigate(
                R.id.nav_graph_comments,
                CommentsFragmentArgs(args.articleId, article).toBundle(),
                navOptions {
                    launchSingleTop = true
                },
            )
            true
        }
        R.id.share -> {
            startShareArticleIntent(requireContext(), article)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun updateBookmarkMenuItem(item: MenuItem, bookmark: Boolean) {
        val (title, iconTint) = if (bookmark) {
            Pair(R.string.remove_bookmark, R.attr.colorControlActivated)
        } else Pair(R.string.add_bookmark, R.attr.colorControlNormal)
        item.setTitle(title)
        item.icon = item.icon.tintDrawable(binding.toolbar.context.getThemedColor(iconTint))
    }

}