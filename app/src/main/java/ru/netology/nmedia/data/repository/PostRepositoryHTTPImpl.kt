package ru.netology.nmedia.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.listWithBaseUrl
import ru.netology.nmedia.utils.ifNotEmpty
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryHTTPImpl(private val context: Context) : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(PostRepositoryInterceptor)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}


    companion object {
        private const val BASE_URL = "http://192.168.222.73:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                        return
                    }

                    val data: List<Post>? = response.body?.string()?.let {
                        gson.fromJson<List<Post>?>(it, object : TypeToken<List<Post>>() {}.type)
                            .listWithBaseUrl(BASE_URL)
                    }



                    data?.apply {
                        callback.onSuccess(this)
                    } ?: run {
                        callback.onError(Exception("Body is null"))
                    }
                }
            })
    }

    fun getPostById(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/${id}")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                        return
                    }

                    val data: Post? = response.body?.string()
                        .let<String?, Post?> {
                            gson.fromJson<Post?>(it, object : TypeToken<Post>() {}.type)
                                .withBaseUrl(BASE_URL)
                        }

                    data?.apply {
                        callback.onSuccess(this)
                    } ?: run {
                        callback.onError(Exception("body is null"))
                    }
                }
            })
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .post(FormBody.Builder().build())
            .url("${BASE_URL}/api/slow/posts/${id}/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                        return
                    }

                    val data: Post? = response.body?.string()
                        .let {
                            gson.fromJson<Post?>(it, object : TypeToken<Post>() {}.type)
                                ?.withBaseUrl(BASE_URL)
                        }

                    data?.apply {
                        callback.onSuccess(this)
                    } ?: run {
                        callback.onError(Exception("body is null"))
                    }
                }

            })
    }

    override fun unlikeById(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/${id}/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                        return
                    }

                    val data: Post? = response.body?.string()
                        .let {
                            gson.fromJson<Post?>(it, object : TypeToken<Post>() {}.type)
                                ?.withBaseUrl(BASE_URL)
                        }

                    data?.apply {
                        callback.onSuccess(this)
                    } ?: run {
                        callback.onError(Exception("body is null"))
                    }
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/${id}")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                        return
                    }

                    callback.onSuccess(Unit)
                }

            })
    }

    override fun addOrEditPost(post: Post, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                        return
                    }

                    val data: Post? = response.body?.string()
                        .let {
                            gson.fromJson<Post?>(it, object : TypeToken<Post>() {}.type)
                                ?.withBaseUrl(BASE_URL)
                        }

                    data?.apply {
                        callback.onSuccess(this)
                    } ?: run {
                        callback.onError(Exception("body is null"))
                    }
                }
            })
    }
}

object PostRepositoryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        Log.i(
            PostRepositoryInterceptor::class.simpleName,
            "Sending request ${request.url}"
        )
        val response: Response = chain.proceed(request)

        val msg =
            "Received response with code ${response.code} for request ${request.url}${response.message.ifNotEmpty { " with message ${response.message}" }}${if (request.headers.size > 0) " with headers ${request.headers}" else ""} "
        Log.i(PostRepositoryInterceptor::class.simpleName, msg)
        if (response.code / 100 != 2) {
            response.close()
            throw IOException(msg)
        }
        return response
    }

}