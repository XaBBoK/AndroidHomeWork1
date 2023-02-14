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

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw java.lang.RuntimeException("body is null")
            }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    fun getPostById(id: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/${id}")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw java.lang.RuntimeException("body is null")
            }
            .let {
                gson.fromJson(it, object : TypeToken<Post>() {}.type)
            }
    }

    override fun likeById(id: Long): Post {
        val request: Request = Request.Builder()
            .post(FormBody.Builder().build())
            .url("${BASE_URL}/api/slow/posts/${id}/likes")
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                return gson.fromJson(response.body?.string(), object : TypeToken<Post>() {}.type)
            }
    }

    override fun unlikeById(id: Long): Post {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/${id}/likes")
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                return gson.fromJson(response.body?.string(), object : TypeToken<Post>() {}.type)
            }

    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/${id}")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun addOrEditPost(post: Post): Post {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                return gson.fromJson(response.body?.string(), object : TypeToken<Post>() {}.type)
            }
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