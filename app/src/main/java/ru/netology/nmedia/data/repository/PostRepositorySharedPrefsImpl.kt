package ru.netology.nmedia.data.repository

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import java.util.*

class PostRepositorySharedPrefsImpl(context: Context) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId = 0L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    private val defaultPosts = List(5) {
        Post(
            id = ++nextId,
            author = "Нетология ${nextId.toLong()}. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с чем-то еще",
            published = "15 окт в 15:28",
            likedByMe = false,
            likes = 999,
            shares = 2999,
        )
    }.onEach { if (it.id == 2L) it.video = "https://www.youtube.com/watch?v=WhWc3b3KhnY" }

    init {
        prefs.getString(key, null).let {
            posts = if (it != null) {
                Toast.makeText(context, "Loaded posts from sharedPrefs!", Toast.LENGTH_LONG).show()
                gson.fromJson(it, type)
            } else {
                Toast.makeText(context, "Loaded default DEMO posts!", Toast.LENGTH_LONG).show()
                defaultPosts
            }

            nextId = posts.maxOf { it.id }.plus(1)
            data.value = posts
        }
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            commit()
        }

    }




    //private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> {
        return Transformations.map(data) { input -> input.sortedByDescending { it.id } }
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id == id) it.copy(
                likedByMe = !it.likedByMe, likes = it.likes + if (it.likedByMe) -1 else 1
            )
            else it
        }

        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id == id) it.copy(shares = it.shares + 10)
            else it
        }

        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    override fun addOrEditPost(post: Post) {
        if (post.id == NON_EXISTING_POST_ID) {
            posts = posts + listOf<Post>(
                Post(
                    //id = (posts.maxOfOrNull { it.id }?.plus(1)) ?: 0,
                    id = nextId++,
                    author = "him",
                    content = post.content,
                    published = Date().toString(),
                    likedByMe = false,
                    likes = 0,
                    shares = 0
                )
            )
            //data.value = posts
        } else {
            posts = posts.map {
                if (it.id == post.id)
                    it.copy(content = post.content)
                else
                    it
            }
        }
        data.value = posts
        sync()

    }
}