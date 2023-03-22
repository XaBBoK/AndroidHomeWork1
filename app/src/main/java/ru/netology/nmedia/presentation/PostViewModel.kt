package ru.netology.nmedia.presentation

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.*
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.SingleLiveEvent

class PostViewModel(
    private val repository: PostRepository,
    private val savedStateHandler: SavedStateHandle,
    //private val scope: CoroutineScope = MainScope()
) : ViewModel() {
    val edited: MutableLiveData<Post?> = MutableLiveData()
    var draft: String? = null

    private val _state = MutableLiveData<ScreenState>(ScreenState.Working())

    val data: LiveData<FeedModel> = repository.data.map { FeedModel(it) }

    val state: LiveData<ScreenState>
        get() = _state

    private val _fragmentEditPostEdited = SingleLiveEvent<Unit>()

    val fragmentEditPostEdited: LiveData<Unit>
        get() = _fragmentEditPostEdited

    fun changeState(newState: ScreenState) {
        _state.postValue(newState)
    }

    fun likeById(id: Long) {
        (data.value?.posts?.firstOrNull { it.id == id })?.let { post ->
            viewModelScope.launch {
                runCatching {
                    //changeState(ScreenState.Loading())

                    if (!post.likedByMe) {
                        repository.likeById(id)
                    } else {
                        repository.unlikeById(id)
                    }
                }

                    //changeState(ScreenState.Working())
                .onFailure { e ->
                    changeState(
                        ScreenState.Error(
                            message = e.message.toString(),
                            repeatText = "Ошибка при обработке лайка!"
                        ) {
                            likeById(id)
                        }
                    )
                }
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            runCatching {
                //changeState(ScreenState.Loading())
                repository.removeById(id)
                //changeState(ScreenState.Working())
            }.onFailure { e ->
                changeState(
                    ScreenState.Error(
                        message = e.message.toString(),
                        repeatText = "Ошибка при удалении!"
                    ) {
                        removeById(id)
                    }
                )
            }
        }
    }

    fun addOrEditPost(post: Post) {
        var addingPost = post
        edited.value?.let {
            addingPost = it.copy(content = post.content)
        }

        viewModelScope.launch {
            try {
                //changeState(ScreenState.Loading())
                repository.addOrEditPost(addingPost)
                edited.postValue(null)
                //changeState(ScreenState.Working(moveRecyclerViewPointerToTop = (post.id == NON_EXISTING_POST_ID)))
                _fragmentEditPostEdited.postValue(Unit)
            } catch (e: Exception) {
                _fragmentEditPostEdited.postValue(Unit)
                changeState(ScreenState.Error(e.message.toString()))
            }
        }
    }

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                changeState(ScreenState.Loading)
                repository.getAll()
                changeState(ScreenState.Working(true))
            } catch (e: Exception) {
                changeState(ScreenState.Error(e.message.toString(), needReload = true))
            }
        }
    }

    fun editPost(post: Post) {
        try {
            edited.value = post
        } catch (e: Exception) {
            changeState(ScreenState.Error(e.message.toString()))
        }
    }

    fun cancelEditPost() {
        edited.value = null
    }

    class Factory(
        owner: SavedStateRegistryOwner,
        private val repo: PostRepository,
        defaultArgs: Bundle? = null

    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        override fun <T : ViewModel> create(
            key: String, modelClass: Class<T>, handle: SavedStateHandle
        ): T {
            @Suppress("UNCHECKED_CAST") return PostViewModel(
                repository = repo, savedStateHandler = handle
            ) as T
        }
    }
}
