package com.xently.utilities.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xently.utilities.ui.fragments.ListFragment.OnSearchDataReceivedListener;

public abstract class SearchableActivity extends AppCompatActivity implements OnSearchDataReceivedListener {
    private Bundle searchData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    public boolean onSearchRequested() {
        startSearch(null, false, searchData, false);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onSearchDataReceived(@Nullable Bundle searchData) {
        this.searchData = searchData;
    }

    public abstract void onSearchIntentReceived(@NonNull String query, @Nullable Bundle searchData);

    private void handleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query == null || query.isEmpty()) return;
            Bundle metadata = intent.getBundleExtra(SearchManager.APP_DATA);
            onSearchIntentReceived(query, metadata);
        }
    }
}