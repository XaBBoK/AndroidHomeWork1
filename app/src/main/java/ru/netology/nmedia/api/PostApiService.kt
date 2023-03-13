package ru.netology.nmedia.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.ifNotEmpty
import java.io.IOException
import java.util.concurrent.TimeUnit

private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .let {
        if (BuildConfig.DEBUG) {
            it.addInterceptor(logging)
        } else
            it
    }
    .addInterceptor(PostApiInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL_API)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

interface PostApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Long): Call<Unit>

    @POST("posts")
    fun addOrEditPost(@Body post: Post): Call<Post>

    @GET("posts/{id}")
    fun getPostById(@Path("id") id: Long): Call<Post>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun unlikeById(@Path("id") id: Long): Call<Post>
}

object PostApi {
    val service: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}

object PostApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        Log.i(
            PostApiInterceptor::class.simpleName,
            "Sending request ${request.url}"
        )

        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            throw IOException("Error on request URL\"${request.url}\": ${e.message}")
        }

        val msg =
            "Received response with code ${response.code} for request ${request.url}${response.message.ifNotEmpty { " with message ${response.message}" }}${if (request.headers.size > 0) " with headers ${request.headers}" else ""} "

        Log.i(PostApiInterceptor::class.simpleName, msg)

        if (!response.isSuccessful) {
            response.close()
            throw IOException(msg)
        }

        return response
    }

}

