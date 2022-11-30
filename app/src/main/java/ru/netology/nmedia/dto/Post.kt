package ru.netology.nmedia.dto

const val NON_EXISTING_POST_ID = -1L

data class Post(
    val id: Long = NON_EXISTING_POST_ID,
    val author: String = "",
    val content: String = "",
    val published: String = "",
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0
)
