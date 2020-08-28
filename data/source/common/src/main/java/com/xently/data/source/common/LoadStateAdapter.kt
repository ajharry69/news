package com.xently.data.source.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.xently.data.source.common.databinding.LoadStateItemBinding

class LoadStateAdapter<T : Any, VH : ViewHolder>(private val adapter: PagingDataAdapter<T, VH>) :
    androidx.paging.LoadStateAdapter<LoadStateAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        ViewHolder.create(parent) { adapter.retry() }

    class ViewHolder(
        private val binding: LoadStateItemBinding,
        private val retryCallback: () -> Unit = {},
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retry.setOnClickListener { retryCallback.invoke() }
        }

        fun bind(state: LoadState) {
            binding.state = state
            binding.errorMessage = (state as? LoadState.Error)?.error?.localizedMessage
        }

        companion object {
            fun create(vg: ViewGroup, retryCallback: () -> Unit = {}): ViewHolder {
                val layoutInflater = LayoutInflater.from(vg.context)
                return ViewHolder(LoadStateItemBinding.inflate(layoutInflater, vg, false),
                    retryCallback)
            }
        }
    }
}