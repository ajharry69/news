package com.xently.utilities.ui.fragments;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class Fragment extends androidx.fragment.app.Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected OnBackPressedCallback mOnBackPressedCallback;
    protected OnBackPressedDispatcher mOnBackPressedDispatcher;

    public Fragment() {
        super();
    }

    public Fragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        mOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        };

        mOnBackPressedDispatcher.addCallback(this, mOnBackPressedCallback);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected void onBackPressed() {
        mOnBackPressedCallback.setEnabled(false);
        mOnBackPressedDispatcher.onBackPressed();
    }

    protected void setupToolbar(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
    }
}