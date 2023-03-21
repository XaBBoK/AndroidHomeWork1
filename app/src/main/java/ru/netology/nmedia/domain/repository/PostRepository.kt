package ru.netology.nmedia.domain.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun addOrEditPost(post: Post)

    val data: LiveData<List<Post>>
}