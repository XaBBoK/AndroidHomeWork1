package ru.netology.nmedia.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.nmedia.utils.AttachmentConverter

@Entity(tableName = "PostEntity")
@TypeConverters(AttachmentConverter::class)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String = "",
    val authorAvatar: String,
    val content: String = "",
    val published: Long = 0L,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    var video: String = "",
    //@Embedded
    var attachment: Attachment? = null,
    var visible: Boolean = true
) {


    companion object {
        fun fromDto(dto: Post): PostEntity {
            dto.apply {
                return PostEntity(
                    id = id,
                    author = author,
                    authorAvatar = authorAvatar,
                    content = content,
                    published = published,
                    likedByMe = likedByMe,
                    likes = likes,
                    shares = shares,
                    video = video,
                    attachment = attachment
                )
            }
        }

        fun toDto(entity: PostEntity) : Post {
            entity.apply {
                return Post(
                    id = id,
                    author = author,
                    content = content,
                    published = published,
                    likedByMe = likedByMe,
                    likes = likes,
                    shares = shares,
                    video = video,
                    authorAvatar = authorAvatar,
                    attachment = attachment,
                )
            }
        }
    }
}

fun List<PostEntity>.toDto() : List<Post> = map { PostEntity.toDto(it) }

fun List<Post>.toEntity() : List<PostEntity> = map(PostEntity::fromDto)