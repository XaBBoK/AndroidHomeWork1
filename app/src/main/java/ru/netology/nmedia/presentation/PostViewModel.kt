package ru.netology.nmedia.presentation

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.File

class PostViewModel(
    private val repository: PostRepository,
    private val savedStateHandler: SavedStateHandle,
    //private val scope: CoroutineScope = MainScope()
) : ViewModel() {
    val edited: MutableLiveData<Post?> = MutableLiveData(null)
    private val emptyMedia = MediaModel()

    private val _media = MutableLiveData<MediaModel>(emptyMedia)
    val media: LiveData<MediaModel>
        get() = _media

    var draft: Post? = null

    private val _state = MutableLiveData<ScreenState>(ScreenState.Working())

    val data: LiveData<FeedModel> =
        repository.data
            .map { FeedModel(it) }
            .asLiveData(Dispatchers.Default)

    val newerCount: LiveData<Long> = data.switchMap {
        repository.getNewerCount(it.posts.maxOfOrNull { post -> post.id } ?: 0L)
            .catch { e ->
                e.printStackTrace()
            }
            .asLiveData(Dispatchers.Default)
    }

    val state: LiveData<ScreenState>
        get() = _state

    private val _fragmentEditPostEdited = SingleLiveEvent<Unit>()

    val fragmentEditPostEdited: LiveData<Unit>
        get() = _fragmentEditPostEdited

    fun changePhoto(file: File, uri: Uri) {
        _media.value = MediaModel(file = file, uri = uri)
    }

    fun clearPhoto() {
        _media.value = emptyMedia
    }

    fun changeState(newState: ScreenState) {
        _state.postValue(newState)
    }

    fun setAllVisible() {
        viewModelScope.launch {
            repository.setAllVisible()
            changeState(ScreenState.Working(moveRecyclerViewPointerToTop = true))
        }
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
                media.value?.let { media ->
                    when (media) {
                        emptyMedia -> repository.addOrEditPost(addingPost)
                        else -> repository.addOrEditPostWithAttachment(addingPost, media)
                    }

                    //changeState(ScreenState.Loading())
                    edited.postValue(null)
                    clearPhoto()

                    if (post.id == NON_EXISTING_POST_ID)
                        repository.setAllVisible()

                    changeState(ScreenState.Working(moveRecyclerViewPointerToTop = (post.id == NON_EXISTING_POST_ID)))
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
