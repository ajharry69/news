package com.xently.articles.comments.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.xently.articles.comments.data.repository.ICommentsRepository

class CommentsViewModel @ViewModelInject constructor(private val repository: ICommentsRepository) :
    ViewModel() {
    // TODO: Implement the ViewModel
}