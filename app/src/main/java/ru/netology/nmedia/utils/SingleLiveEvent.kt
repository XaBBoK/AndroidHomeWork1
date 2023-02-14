package ru.netology.nmedia.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        require(!hasActiveObservers()) {
            error("Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner) {
            if (pending) {
                pending = false
                observer.onChanged(it)
            }
        }
    }

    override fun setValue(value: T?) {
        //т.к. PostViewModel.addOrEditPost вызывается и из FeedFragment, и из FragmentEditPost,
        //то устанавливаем значение только при наличии обзервера
        if (hasActiveObservers()) {
            pending = true
            super.setValue(value)
        }
    }
}