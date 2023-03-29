package ru.netology.nmedia.presentation

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.netology.nmedia.dto.Post

/*private const val POST_LIVE_DATA_SAVED_STATE_IDENTIFICATION = "POST_LIVE_DATA_SAVED_STATE"

class EditPostViewModel(post: Post, private val savedStateHandler: SavedStateHandle) : ViewModel() {
    val data = savedStateHandler.getLiveData(POST_LIVE_DATA_SAVED_STATE_IDENTIFICATION, post)

    var content: String
        get() {
            return data.value?.content ?: ""
        }
        set(value) {
            data.value = data.value?.copy(content = value)
        }

    class Factory(
        owner: SavedStateRegistryOwner,
        private val post: Post,
        defaultArgs: Bundle? = null

    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            @Suppress("UNCHECKED_CAST")
            return EditPostViewModel(post = post, savedStateHandler = handle) as T
        }
    }

}*/