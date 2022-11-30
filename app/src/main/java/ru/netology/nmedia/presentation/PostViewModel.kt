package ru.netology.nmedia.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val data = repository.getAll()
    val edited: MutableLiveData<Post?> = MutableLiveData()


    /*fun like() = repository.like()
    fun share() = repository.share()*/
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun addOrEditPost(post: Post) {
        var addingPost = post
        edited.value?.let {
            addingPost = it.copy(content = post.content)
        }.also { edited.value = null }

        repository.addOrEditPost(addingPost)
    }

    fun editPost(post: Post) {
        edited.value = post
    }

    fun cancelEditPost() {
        edited.value = null
    }


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
