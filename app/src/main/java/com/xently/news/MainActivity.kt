package com.xently.news

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import com.xently.news.databinding.MainActivityBinding
import com.xently.utilities.ui.SearchableActivity
import dagger.hilt.android.AndroidEntryPoint

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

    }
}