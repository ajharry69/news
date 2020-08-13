package com.xently.utilities.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListFragment extends Fragment {
    public static final String ARG_KEY_SEARCH_IDENTIFIER = String.format("%s.ARG_KEY_SEARCH_IDENTIFIER", ListFragment.class.getName());
    private OnSearchDataReceivedListener onSearchDataReceivedListener;

    public ListFragment() {
        super();
    }

    public ListFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            getActivity().setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onSearchDataReceivedListener != null)
            onSearchDataReceivedListener.onSearchDataReceived(onCreateSearchData());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchDataReceivedListener)
            onSearchDataReceivedListener = (OnSearchDataReceivedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSearchDataReceivedListener = null;
    }

    @Nullable
    public Bundle onCreateSearchData() {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_SEARCH_IDENTIFIER, onCreateSearchIdentifier());
        return bundle;
    }

    @NonNull
    public String onCreateSearchIdentifier() {
        return TAG;
    }

    public interface OnSearchDataReceivedListener {
        void onSearchDataReceived(@Nullable Bundle searchData);
    }
}
