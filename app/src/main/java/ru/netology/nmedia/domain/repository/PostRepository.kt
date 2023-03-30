package ru.netology.nmedia.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.MediaModel

interface PostRepository {
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun addOrEditPost(post: Post)

    suspend fun addOrEditPostWithAttachment(post: Post, media: MediaModel)

    val data: Flow<List<Post>>

    fun getNewerCount(id: Long) : Flow<Long>

    suspend fun setAllVisible()
}