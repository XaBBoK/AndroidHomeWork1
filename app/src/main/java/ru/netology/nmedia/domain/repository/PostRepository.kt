package ru.netology.nmedia.domain.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll() : List<Post>
    fun likeById(id: Long) : Post
    fun unlikeById(id: Long) : Post
    fun removeById(id: Long)
    fun addOrEditPost(post: Post) : Post
}