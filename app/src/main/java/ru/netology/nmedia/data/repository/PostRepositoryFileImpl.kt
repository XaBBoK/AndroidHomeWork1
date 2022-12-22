package ru.netology.nmedia.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.R
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import java.util.*

class PostRepositoryFileImpl(private val context: Context) :
    PostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private var nextId = 0L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    private val defaultPosts = List(5) {
        Post(
            id = ++nextId,
            author = "Нетология ${nextId}. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с чем-то еще",
            published = "15 окт в 15:28",
            likedByMe = false,
            likes = 999,
            shares = 2999,
        )
    }.onEach { if (it.id == 2L) it.video = "https://www.youtube.com/watch?v=WhWc3b3KhnY" }

    init {
        try {
            val file = context.filesDir.resolve(filename)
            file.bufferedReader().use {
                posts = gson.fromJson(it, type)
            }
        } catch (e: Exception) {
            Log.w(
                "Settings", "Error while loading file [${filename}]: ${e.message}" +
                        "\nLoading default settings from DEMO posts!"
            )
            Toast.makeText(context, "Loaded default DEMO posts!", Toast.LENGTH_LONG).show()
            posts = defaultPosts
        }

        nextId = posts.maxOf { it.id }.plus(1)
        data.value = posts

        sync()
    }


    private fun sync() {
        try {
            val file = context.filesDir.resolve(filename)
            file.bufferedWriter().use {
                it.write(gson.toJson(posts))
            }
        } catch (e: Exception) {
            Log.e("Settings", "Error while saving file [${filename}]: ${e.message}")
            Toast.makeText(
                context,
                context.getString(R.string.settings_save_error_message),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }


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