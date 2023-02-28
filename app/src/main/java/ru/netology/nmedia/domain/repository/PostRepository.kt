package ru.netology.nmedia.domain.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(callback: Callback<List<Post>>)
    fun likeById(id: Long, callback: Callback<Post>)
    fun unlikeById(id: Long, callback: Callback<Post>)
    fun removeById(id: Long, callback: Callback<Unit>)
    fun addOrEditPost(post: Post, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(data: T)
        fun onError(e: Exception)
    }
}