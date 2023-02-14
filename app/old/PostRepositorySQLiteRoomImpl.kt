package ru.netology.nmedia.data.repository.old

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity

class PostRepositorySQLiteRoomImpl(context: Context) : PostRepository {
    private val dao: PostDao = AppDb.getInstance(context).postDao()
    private val _data = MutableLiveData(emptyList<Post>())

    val data: LiveData<List<Post>>
        get() = _data

    init {
        //data.value = getAll().value
    }

    override fun getAll() {
        _data.value = Transformations.map(dao.getAll()) { list ->
            list.map {
                PostEntity.toDto(it)
            }
        }.value
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun addOrEditPost(post: Post) {
        dao.addOrEditPost(PostEntity.fromDto(post))
    }


}