package ru.netology.nmedia.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity

class PostRepositorySQLiteRoomImpl(context: Context) : PostRepository {
    private val dao: PostDao = AppDb.getInstance(context).postDao()
    private val data = MutableLiveData(emptyList<Post>())

    init {
        data.value = getAll().value
    }

    override fun getAll(): LiveData<List<Post>> = Transformations.map(dao.getAll()) { list ->
        list.map {
            PostEntity.toDto(it)
        }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
        data.value = data.value?.map {
            if (it.id == id) it.copy(
                likedByMe = !it.likedByMe, likes = it.likes + if (it.likedByMe) -1 else 1
            )
            else it
        }

    }

    override fun shareById(id: Long) {
        dao.shareById(id)

        data.value = data.value?.map {
            if (it.id == id) it.copy(shares = it.shares + 10)
            else it
        }
    }

    override fun removeById(id: Long) {
        dao.removeById(id)

        data.value = data.value?.filter { it.id != id }
    }

    override fun addOrEditPost(post: Post) {
        dao.addOrEditPost(PostEntity.fromDto(post))

        if (post.id == NON_EXISTING_POST_ID) {
            data.value = data.value?.plus(post)
        } else {
            data.value = data.value?.map {
                if (it.id == post.id)
                    post
                else
                    it
            }
        }
    }
}