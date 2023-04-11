package ru.netology.nmedia.api

import kotlinx.coroutines.CancellationException
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.*
import ru.netology.nmedia.presentation.AuthModel
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
    .addInterceptor(PostApiAuthInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL_API)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

interface PostApiService {
    @Multipart
    @POST("users/registration")
    suspend fun registerUser(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part? = null,
    ): Response<AuthModel>

    /*@FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthModel>*/

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun auth(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthModel>

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part part: MultipartBody.Part): Response<Media>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts")
    suspend fun addOrEditPost(@Body post: Post): Response<Post>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>
}

object PostApi {
    val service: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}

object PostApiAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = AppAuth.getInstance().data.value?.token?.let {
            chain.request().newBuilder()
                .addHeader("Authorization", it)
                .build()
        } ?: chain.request()

        return chain.proceed(request)
    }
}

object PostApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request()

        val response: okhttp3.Response = try {
            chain.proceed(request)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw NetworkAppError
        } catch (e: Exception) {
            throw UnknownAppError
        }

        if (!response.isSuccessful) {
            response.close()
            throw ApiAppError(response.code, response.message)
        }

        return response
    }

}

