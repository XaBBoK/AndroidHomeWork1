package ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.ApiAuthInterceptor
import ru.netology.nmedia.api.ApiInterceptor
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.data.repository.PostRepositoryHTTPImpl
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.domain.repository.PostRepository
import java.util.concurrent.TimeUnit

class DependencyContainer(context: Context) {
    companion object {
        @Volatile
        private var instance: DependencyContainer? = null

        fun initApp(context: Context) {
            instance = DependencyContainer(context)
        }

        fun getInstance(): DependencyContainer {
            return instance!!
        }
    }

    private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        //.allowMainThreadQueries()
        .build()

    private val postDao = appDb.postDao()

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
        .addInterceptor(ApiInterceptor)
        .addInterceptor(ApiAuthInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL_API)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    val repository: PostRepository = PostRepositoryHTTPImpl(postDao, apiService)

    private val workManager = WorkManager.getInstance(context)

    val appAuth = AppAuth(context, workManager)
}