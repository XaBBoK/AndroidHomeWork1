package ru.netology.nmedia.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.domain.repository.PostRepository

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val data = repository.getAll()

    /*fun like() = repository.like()
    fun share() = repository.share()*/
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)

    class Factory(private val repo: PostRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PostViewModel(repo) as T
            }
            throw IllegalArgumentException("Wrong view-model in Factory")
        }
    }
}
