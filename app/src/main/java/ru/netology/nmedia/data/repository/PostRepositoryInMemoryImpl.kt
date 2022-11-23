package ru.netology.nmedia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = List(100) {
        Post(
            id = it.toLong(),
            author = "Нетология ${it.toLong()}. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с чем-то еще",
            published = "15 окт в 15:28",
            likedByMe = false,
            likes = 999,
            shares = 2999,
        )}


    private val data = MutableLiveData(posts)

    /*override fun get(): LiveData<Post> {
        return data
    }*/

    /*override fun like() {
        post = post.copy(
            likedByMe = !post.likedByMe,
            likes = post.likes + if (post.likedByMe) 1 else -1
        )
        data.value = post
    }

    override fun share() {
        post = post.copy(shares = post.shares + 10)
        data.value = post
    }*/

    override fun getAll(): LiveData<List<Post>> {
        return data
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id == id)
                it.copy(
                    likedByMe = !it.likedByMe,
                    likes = it.likes + if (it.likedByMe) -1 else 1
                )
            else
                it
        }

        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id == id)
                it.copy(shares = it.shares + 10)
            else it
        }

        data.value = posts
    }

}
