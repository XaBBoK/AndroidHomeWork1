package ru.netology.nmedia.data.repository

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post

class PostRepositoryHTTPImpl(private val context: Context) : PostRepository {
    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        PostApi.service.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val data: List<Post>? = response.body()

                data?.apply {
                    callback.onSuccess(this)
                } ?: run {
                    callback.onError(Exception("Body is null"))
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    fun getPostById(id: Long, callback: PostRepository.Callback<Post>) {
        PostApi.service.getPostById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                val data: Post? = response.body()

                data?.apply {
                    callback.onSuccess(this)
                } ?: run {
                    callback.onError(Exception("body is null"))
                }

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostApi.service.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                val data: Post? = response.body()

                data?.apply {
                    callback.onSuccess(this)
                } ?: run {
                    callback.onError(Exception("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun unlikeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostApi.service.unlikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                val data: Post? = response.body()

                data?.apply {
                    callback.onSuccess(this)
                } ?: run {
                    callback.onError(Exception("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostApi.service.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                callback.onSuccess(Unit)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }

    override fun addOrEditPost(post: Post, callback: PostRepository.Callback<Post>) {
        PostApi.service.addOrEditPost(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }

                val data: Post? = response.body()

                data?.apply {
                    callback.onSuccess(this)
                } ?: run {
                    callback.onError(Exception("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }
}

