package ru.netology.nmedia.presentation

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList()
)

sealed class ScreenState() {
    object Loading : ScreenState()
    class Error(val message: String, val needReload: Boolean = false, val repeatText: String? = null, val repeatAction: ( () -> Unit)? = null)   : ScreenState()
    class Working(val moveRecyclerViewPointerToTop: Boolean = false) : ScreenState()
}
