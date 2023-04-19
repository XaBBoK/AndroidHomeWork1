package ru.netology.nmedia.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.PostRepository

//class ViewModelFactory(
//    private val repository: PostRepository,
//    private val appAuth: AppAuth,
//    private val apiService: ApiService
//) : ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T =
//        when {
//            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
//                PostViewModel(
//                    repository,
//                    appAuth
//                ) as T
//            }
//
//            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
//                AuthViewModel(appAuth, apiService) as T
//            }
//
//            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
//                SignUpViewModel(apiService, appAuth) as T
//            }
//
//            else -> error("Unknown class: $modelClass")
//        }
//
//}