package ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val NON_EXISTING_POST_ID = 0L

@Parcelize
data class Post(
    val id: Long = NON_EXISTING_POST_ID,
    val author: String = "",
    val content: String = "",
    val published: Long = 0L,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    var video: String = "",
    val authorAvatar: String = ""
) : Parcelable {

    fun isNewPost(): Post? {
        return if (this.id == NON_EXISTING_POST_ID) this else null
    }

    fun withBaseUrl(baseUrl: String): Post {
        return this.copy(authorAvatar = "${baseUrl}/avatars/${authorAvatar}")
    }


    companion object {
        fun fromDto(dto: Post): PostEntity {
            dto.apply {
                return PostEntity(id, author, content, published, likedByMe, likes, shares, video)
            }
        }

        fun toDto(entity: PostEntity): Post {
            entity.apply {
                return Post(id, author, content, published, likedByMe, likes, shares, video)
            }
        }
    }
}

fun List<Post>.listWithBaseUrl(baseUrl: String) : List<Post> = this.map {
    it.withBaseUrl(baseUrl)
}