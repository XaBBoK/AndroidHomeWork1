package ru.netology.nmedia.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity
import ru.netology.nmedia.dto.toDto

class PostRepositoryHTTPImpl(private val context: Context) : PostRepository {
    private val dao: PostDao = AppDb.getInstance(context).postDao()
    override val data: LiveData<List<Post>> = dao.getAll().map { it.toDto() }
    override suspend fun getAll() {
        val response = PostApi.service.getAll()

        val posts = response.body().orEmpty()
        dao.clearAndInsert(posts.map { PostEntity.fromDto(it) })
    }

    suspend fun getPostById(id: Long) {
        val response = PostApi.service.getPostById(id)
        response.body()?.also {
            dao.insert(PostEntity.fromDto(it))
        }
    }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
        runCatching {
            PostApi.service.likeById(id)
        }.onFailure {
            dao.unlikeById(id)
            throw it
        }
    }

    override suspend fun unlikeById(id: Long) {
        dao.unlikeById(id)

        runCatching {
            PostApi.service.unlikeById(id)
        }.onFailure {
            dao.likeById(id)
            throw it
        }
    }

    override suspend fun removeById(id: Long) {
        val post = dao.getById(id)
        post.let { originalPost ->
            dao.removeById(id)
            runCatching {
                PostApi.service.removeById(id)
            }.onFailure {
                dao.addOrEditPost(originalPost)
                throw it
            }
        }
    }

    override suspend fun addOrEditPost(post: Post) {
        PostApi.service.addOrEditPost(post).body()?.also { newPost: Post ->
            dao.addOrEditPost(PostEntity.fromDto(newPost))
        }
    }
}

