package ru.netology.nmedia.presentation

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.*
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.SingleLiveEvent
import ru.netology.nmedia.utils.ensureNotOnMainThread
import kotlin.concurrent.thread

class PostViewModel(
    private val repository: PostRepository,
    private val savedStateHandler: SavedStateHandle
) : ViewModel() {
    val edited: MutableLiveData<Post?> = MutableLiveData()
    var draft: String? = null

    private val _state = MutableLiveData<ScreenState>(ScreenState.Working())
    private val _data = MutableLiveData<List<Post>>(emptyList())

    val data: LiveData<List<Post>>
        get() = _data

    val state: LiveData<ScreenState>
        get() = _state

    private val _fragmentEditPostEdited = SingleLiveEvent<Unit>()

    val fragmentEditPostEdited: LiveData<Unit>
        get() = _fragmentEditPostEdited

    fun changeState(newState: ScreenState) {
        _state.postValue(newState)
    }

    fun likeById(id: Long) {
        thread {
            try {
                changeState(ScreenState.Loading)
                _data.value?.filter { post -> post.id == id }
                    ?.firstOrNull()?.likedByMe?.let { likedByMe ->
                        if (likedByMe) {
                            repository.unlikeById(id)
                        } else {
                            repository.likeById(id)
                        }
                    }?.let { post ->
                        _data.value?.map {
                            if (id == it.id) post
                            else it
                        }.let {
                            _data.postValue(it)
                        }
                    }.let {
                        changeState(ScreenState.Working())
                    }
            } catch (e: Exception) {
                changeState(ScreenState.Error(e.message.toString()))
            }
        }
    }

    fun removeById(id: Long) {
        thread {
            try {
                changeState(ScreenState.Loading)
                repository.removeById(id)

                val posts = _data.value?.filter { post -> (post.id != id) }
                posts?.let {
                    _data.postValue(it)
                    changeState(ScreenState.Working())
                }
            } catch (e: Exception) {
                changeState(ScreenState.Error(e.message.toString()))
            }
        }
    }

    fun addOrEditPost(post: Post) {
        var addingPost = post
        edited.value?.let {
            addingPost = it.copy(content = post.content)
        }

        thread {
            try {
                changeState(ScreenState.Loading)

                repository.addOrEditPost(addingPost).apply {
                    also {
                        if (post.id == NON_EXISTING_POST_ID) {
                            _data.postValue(_data.value?.plus(it))
                            return@apply
                        }
                    }
                    also {
                        _data.postValue(_data.value?.map { if (it.id == post.id) this else it })
                        return@apply
                    }
                }.apply {
                    edited.postValue(null)
                    changeState(ScreenState.Working())
                    _fragmentEditPostEdited.postValue(Unit)
                }

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
        ensureNotOnMainThread {
            try {
                changeState(ScreenState.Loading)
                val posts = repository.getAll()
                _data.postValue(posts)
                posts.let {
                    changeState(ScreenState.Working(true))
                }
            } catch (e: Exception) {
                _data.postValue(
                    emptyList()
                )
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
