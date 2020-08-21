package com.xently.articles.comments.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.xently.models.Comment

class CommentDiffUtil : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
}