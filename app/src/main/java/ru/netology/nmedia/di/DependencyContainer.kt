package ru.netology.nmedia.di

import android.content.Context
import androidx.work.WorkManager
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.PostRepositoryHTTPImpl
import ru.netology.nmedia.repository.PostRepository

//class DependencyContainer(context: Context) {
//    companion object {
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        fun initApp(context: Context) {
//            instance = DependencyContainer(context)
//        }
//
//        fun getInstance(): DependencyContainer {
//            return instance!!
//        }
//    }
//
////    private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
////        .fallbackToDestructiveMigration()
////        //.allowMainThreadQueries()
////        .build()
//
////    private val postDao = appDb.postDao()
//
////    private val logging = HttpLoggingInterceptor().apply {
////        level = HttpLoggingInterceptor.Level.BODY
////    }
////
////    private val client = OkHttpClient.Builder()
////        .connectTimeout(10, TimeUnit.SECONDS)
////        .let {
////            if (BuildConfig.DEBUG) {
////                it.addInterceptor(logging)
////            } else
////                it
////        }
////        .addInterceptor(ApiInterceptor)
////        .addInterceptor(ApiAuthInterceptor)
////        .build()
////
////    private val retrofit = Retrofit.Builder()
////        .baseUrl(BuildConfig.BASE_URL_API)
////        .addConverterFactory(GsonConverterFactory.create())
////        .client(client)
////        .build()
//
////    val apiService: ApiService = retrofit.create(ApiService::class.java)
//
////    val repository: PostRepository = PostRepositoryHTTPImpl(postDao, apiService)
//
////    private val workManager = WorkManager.getInstance(context)
//
////    val appAuth = AppAuth(context, workManager)
//}