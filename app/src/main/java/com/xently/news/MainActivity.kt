package com.xently.news

import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import com.xently.news.databinding.MainActivityBinding
import com.xently.news.providers.SearchSuggestionsProvider
import com.xently.news.ui.list.AbstractArticleListFragment.Companion.SEARCH_IDENTIFIER
import com.xently.news.ui.list.filter.ArticlesFilterResultsFragmentArgs
import com.xently.utilities.ui.SearchableActivity
import com.xently.utilities.ui.fragments.ListFragment.ARG_KEY_SEARCH_IDENTIFIER
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale.ROOT

@AndroidEntryPoint
class MainActivity : SearchableActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (supportFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment)?.apply {
            this@MainActivity.navController = navController
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)

    override fun onSearchIntentReceived(query: String, searchData: Bundle?) {
        SearchRecentSuggestions(
            this,
            SearchSuggestionsProvider.AUTHORITY,
            SearchSuggestionsProvider.MODE
        ).saveRecentQuery(query, null)

        val identifier =
            searchData?.getString(ARG_KEY_SEARCH_IDENTIFIER)?.toLowerCase(ROOT) ?: return

        val navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()

        if (identifier.contains(SEARCH_IDENTIFIER.toLowerCase(ROOT))) {
            // articles search was requested
            navController.navigate(
                R.id.dest_article_list_filter,
                ArticlesFilterResultsFragmentArgs(query).toBundle(),
                navOptions
            )
        }
    }
}