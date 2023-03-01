package ru.netology.nmedia.presentation

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.*
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.SingleLiveEvent

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
        changeState(ScreenState.Loading)

        val callback = object : PostRepository.Callback<Post> {
            override fun onError(e: Exception) {
                changeState(ScreenState.Error(e.message.toString()))
            }

            override fun onSuccess(data: Post) {
                data.let { post ->
                    _data.value?.map {
                        if (id == it.id) post
                        else it
                    }.let {
                        _data.postValue(it)
                    }
                }.let {
                    changeState(ScreenState.Working())
                }
            }
        }

        _data.value?.filter { post -> post.id == id }
            ?.firstOrNull()?.likedByMe?.let { likedByMe ->
                if (likedByMe) {
                    repository.unlikeById(id, callback)
                } else {
                    repository.likeById(id, callback)
                }
            }
    }

    fun removeById(id: Long) {
        changeState(ScreenState.Loading)
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(data: Unit) {
                val posts = _data.value?.filter { post -> (post.id != id) }
                posts?.let {
                    _data.postValue(it)
                    changeState(ScreenState.Working())
                }
            }

            override fun onError(e: Exception) {
                changeState(ScreenState.Error(e.message.toString()))
            }
        })
    }

    fun addOrEditPost(post: Post) {
        var addingPost = post
        edited.value?.let {
            addingPost = it.copy(content = post.content)
        }

        changeState(ScreenState.Loading)
        repository.addOrEditPost(addingPost, object : PostRepository.Callback<Post> {
            override fun onSuccess(data: Post) {
                data.apply {
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
                    changeState(ScreenState.Working(moveRecyclerViewPointerToTop = (post.id == NON_EXISTING_POST_ID)))
                    _fragmentEditPostEdited.postValue(Unit)
                }
            }

            override fun onError(e: Exception) {
                _fragmentEditPostEdited.postValue(Unit)
                changeState(ScreenState.Error(e.message.toString()))
            }
        })
    }

    init {
        loadPosts()
    }

    fun loadPosts() {
        changeState(ScreenState.Loading)

        repository.getAll(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(data)
                changeState(ScreenState.Working(true))
            }

            override fun onError(e: Exception) {
                _data.postValue(
                    emptyList()
                )
                changeState(ScreenState.Error(e.message.toString(), needReload = true))
            }
        })
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
