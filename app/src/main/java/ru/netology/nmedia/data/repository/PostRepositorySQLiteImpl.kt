package ru.netology.nmedia.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(private val context: Context) : PostRepository {
    private val dao = AppDb.getInstance(context).postDao
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> {
        return data
    }

    override fun likeById(id: Long) {
        dao.likeById(id)

        posts = posts.map {
            if (it.id == id) it.copy(
                likedByMe = !it.likedByMe, likes = it.likes + if (it.likedByMe) -1 else 1
            )
            else it
        }

        data.value = posts
    }

    override fun shareById(id: Long) {
        dao.shareById(id)

        posts = posts.map {
            if (it.id == id) it.copy(shares = it.shares + 10)
            else it
        }

        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)

        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun addOrEditPost(post: Post) {
        dao.addOrEditPost(post)
        if (post.id == NON_EXISTING_POST_ID) {
            posts = dao.getAll()
        } else {
            posts = posts.map {
                if (it.id == post.id)
                    post
                else
                    it
            }
        }

        data.value = posts
    }
}