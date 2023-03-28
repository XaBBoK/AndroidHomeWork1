package ru.netology.nmedia.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity
import ru.netology.nmedia.dto.toDto
import ru.netology.nmedia.error.ApiAppError

class PostRepositoryHTTPImpl(private val context: Context) : PostRepository {
    private val dao: PostDao = AppDb.getInstance(context).postDao()
    override val data: Flow<List<Post>> = dao.getAll()
        .map {
            it.filter { postEntity -> postEntity.visible }
                .toDto()
        }
        .flowOn(Dispatchers.Default)

    private val readMutex = Mutex()

    override fun getNewerCount(id: Long): Flow<Long> =
        flow {
            while (true) {
                readMutex.withLock {
                    runCatching {
                        val biggestInvisibleId = dao.getBiggestInvisibleId()
                        //emit(dao.getInvisibleCount())
                        val response = PostApi.service.getNewer(biggestInvisibleId ?: id)

                        val body: List<Post> =
                            response.body() ?: throw ApiAppError(response.code(), response.message())
                        val p = body
                            .map {
                                PostEntity.fromDto(it)
                                    .copy(visible = false)
                            }

                        dao.insertWithoutReplace(p)
                    }.onSuccess {
                        val count = dao.getInvisibleCount()
                        emit(count)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }

                delay(1000)
            }
        }
            .flowOn(Dispatchers.Default)

    override suspend fun setAllVisible() {
        dao.setAllVisible()
    }


    override suspend fun getAll() {
        readMutex.withLock {
            val response = PostApi.service.getAll()
            val posts = response.body().orEmpty()
            dao.clearAndInsert(posts.map { PostEntity.fromDto(it) })
        }
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

