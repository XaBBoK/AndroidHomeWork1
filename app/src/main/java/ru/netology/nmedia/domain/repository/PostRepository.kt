package ru.netology.nmedia.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun addOrEditPost(post: Post)

    val data: Flow<List<Post>>

    fun getNewerCount(id: Long) : Flow<Long>

    suspend fun setAllVisible()
}