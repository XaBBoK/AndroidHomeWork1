package ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.nmedia.BuildConfig

const val NON_EXISTING_POST_ID = 0L

@Parcelize
data class Post(
    val id: Long = NON_EXISTING_POST_ID,
    val author: String = "",
    var content: String = "",
    val published: Long = 0L,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    var video: String = "",
    val authorAvatar: String = "",
    var attachment: Attachment? = null,

    ) : Parcelable {

    fun isNewPost(): Post? {
        return if (this.id == NON_EXISTING_POST_ID) this else null
    }

    fun withBaseUrls(): Post {
        return this.copy(
            authorAvatar = "${BuildConfig.BASE_URL_AVATARS}${authorAvatar}",
            attachment = attachment?.copy(url = "${BuildConfig.BASE_URL_IMAGES}${attachment?.url}")
        )
    }
}

fun List<Post>.listWithBaseUrls(): List<Post> = this.map {
    it.withBaseUrls()
}