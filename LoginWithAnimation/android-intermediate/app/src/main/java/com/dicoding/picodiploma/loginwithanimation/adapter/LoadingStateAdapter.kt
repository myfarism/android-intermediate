package com.dicoding.picodiploma.loginwithanimation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Ketika tombol retry diklik, panggil retry
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.retryButton.isVisible = false
                    binding.errorMsg.isVisible = false
                }
                is LoadState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.retryButton.isVisible = true
                    binding.errorMsg.isVisible = true
                    binding.errorMsg.text = loadState.error.localizedMessage
                }
                else -> {
                    // Menyembunyikan semua elemen jika tidak ada loading atau error
                    binding.progressBar.isVisible = false
                    binding.retryButton.isVisible = false
                    binding.errorMsg.isVisible = false
                }
            }
        }
    }
}

