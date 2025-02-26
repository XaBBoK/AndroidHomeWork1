package ru.netology.nmedia.api

import okhttp3.*
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.error.*
import ru.netology.nmedia.presentation.AuthModel

interface ApiService {
    @POST("users/push-tokens")
    suspend fun savePushToken(@Body pushToken: PushToken): Response<Unit>

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





