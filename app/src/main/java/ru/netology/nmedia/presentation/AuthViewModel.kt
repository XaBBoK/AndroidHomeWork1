package ru.netology.nmedia.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.AppError
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val appAuth: AppAuth, private val apiService: ApiService) :
    ViewModel() {
    val data = appAuth.data.asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = data.value != null

    private val _creds = MutableLiveData<LoginPasswordModel>(LoginPasswordModel("sber", "secret"))
    val creds: LiveData<LoginPasswordModel>
        get() = _creds

    private val _uiState: MutableStateFlow<AuthScreenState> =
        MutableStateFlow(AuthScreenState.AuthScreenNormal)
    val uiState: StateFlow<AuthScreenState> = _uiState.asStateFlow()

    fun login() {
        _uiState.value = AuthScreenState.AuthScreenRequesting()
        viewModelScope.launch {
            runCatching {
                creds.value
                    ?.let {
                        apiService.auth(it.login, it.password).body()
                    }
                    ?.also {
                        //получен токен
                        appAuth.setAuth(it.id, it.token)
                        _uiState.emit(AuthScreenState.AuthScreenMustNavigateUp())
                    }
            }.onFailure {
                //а вдруг юзер закрыл всё и пришло JobCancellationException
                (it as? AppError)?.let { error ->
                    _uiState.emit(AuthScreenState.AuthScreenError(error))
                }
            }
        }
    }
}

sealed class AuthScreenState() {
    object AuthScreenNormal : AuthScreenState()
    class AuthScreenRequesting() : AuthScreenState()
    class AuthScreenError(val error: AppError) : AuthScreenState()
    class AuthScreenMustNavigateUp() : AuthScreenState()
}