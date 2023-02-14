package ru.netology.nmedia.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PostEntity")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String = "",
    val content: String = "",
    val published: Long = 0L,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    var video: String = ""
) {


    companion object {
        fun fromDto(dto: Post): PostEntity {
            dto.apply {
                return PostEntity(id, author, content, published, likedByMe, likes, shares, video)
            }
        }

        fun toDto(entity: PostEntity) : Post {
            entity.apply {
                return Post(id, author, content, published, likedByMe, likes, shares, video)
            }
        }
    }
}