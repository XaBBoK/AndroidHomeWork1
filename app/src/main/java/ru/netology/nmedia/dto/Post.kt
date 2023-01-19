package ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val NON_EXISTING_POST_ID = -1L

@Parcelize
data class Post(
    val id: Long = NON_EXISTING_POST_ID,
    val author: String = "",
    val content: String = "",
    val published: String = "",
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    var video: String = ""
) : Parcelable

fun Post.isNewPost(): Post? {
    return if (this.id == NON_EXISTING_POST_ID) this else null
}

