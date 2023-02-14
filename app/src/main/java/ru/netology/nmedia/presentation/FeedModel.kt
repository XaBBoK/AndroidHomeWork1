package ru.netology.nmedia.presentation

/*data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val refreshing: Boolean = false
)*/

sealed class ScreenState {
    object Loading : ScreenState()
    class Error(val message: String, val needReload: Boolean = false) : ScreenState()
    class Working(val moveRecyclerViewPointerToTop: Boolean = false) : ScreenState()
}
