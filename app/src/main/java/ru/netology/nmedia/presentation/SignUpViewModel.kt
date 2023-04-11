package ru.netology.nmedia.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.AppError
import java.io.File

class SignUpViewModel : ViewModel() {
    private val _data = MutableLiveData(
        SignUpModel(name = "", login = "", password = "", password_confirm = "", avatar = null)
    )

    val data: LiveData<SignUpModel>
        get() = _data

    private val _uiState: MutableStateFlow<SignUpScreenState> =
        MutableStateFlow(SignUpScreenState.SignUpScreenNormal)

    val uiState: Flow<SignUpScreenState> = _uiState

    private fun setScreenState(newState: SignUpScreenState) {
        synchronized(this) {
            viewModelScope.launch {
                _uiState.emit(newState)
            }
        }
    }

    fun changeAvatarPhoto(newFile: File?) {
        _data.postValue(_data.value?.copy(avatar = newFile))
    }

    fun clearErrorState() {
        synchronized(this) {
            viewModelScope.launch {
                if (_uiState.value is SignUpScreenState.SignUpScreenError) {
                    _uiState.emit(SignUpScreenState.SignUpScreenNormal)
                }
            }
        }
    }

    fun register() {
        synchronized(this) {
            viewModelScope.launch {
                runCatching {
                    setScreenState(SignUpScreenState.SignUpScreenRequesting())

                    _data.value
                        ?.let {
                            it.password.ifEmpty {
                                setScreenState(SignUpScreenState.SignUpScreenError(SignUpError.PasswordEmptyError))
                                return@launch
                            }

                            it.password_confirm.ifEmpty {
                                setScreenState(SignUpScreenState.SignUpScreenError(SignUpError.PasswordConfirmEmptyError))
                                return@launch
                            }

                            it.name.ifEmpty {
                                setScreenState(SignUpScreenState.SignUpScreenError(SignUpError.NameEmptyError))
                                return@launch
                            }

                            it.login.ifEmpty {
                                setScreenState(SignUpScreenState.SignUpScreenError(SignUpError.LoginEmptyError))
                                return@launch
                            }

                            if (it.password != it.password_confirm) {
                                //different passwords
                                setScreenState(SignUpScreenState.SignUpScreenError(SignUpError.PasswordsDifferentError))
                                return@launch
                            }

                            //MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())

                            PostApi.service.registerUser(
                                it.login.toRequestBody("text/plain".toMediaType()),
                                it.password.toRequestBody("text/plain".toMediaType()),
                                it.name.toRequestBody("text/plain".toMediaType()),
                                it.avatar?.let { file -> MultipartBody.Part.createFormData("file", file.name, file.asRequestBody()) }
                            ).body()
                        }
                        ?.also {
                            AppAuth.getInstance().setAuth(it.id, it.token)
                            setScreenState(SignUpScreenState.SignUpScreenMustNavigateUp())
                        }
                }.onFailure {
                    //а вдруг юзер закрыл всё и пришло JobCancellationException
                    (it as? AppError)?.let { error ->
                        setScreenState(
                            SignUpScreenState.SignUpScreenError(
                                error = SignUpError.ExceptionError(exception = error)
                            )
                        )
                    }
                }
            }
        }
    }
}

sealed class SignUpScreenState() {
    object SignUpScreenNormal : SignUpScreenState()
    class SignUpScreenRequesting() : SignUpScreenState()
    class SignUpScreenError(val error: SignUpError) : SignUpScreenState()
    class SignUpScreenMustNavigateUp() : SignUpScreenState()
}

sealed class SignUpError {
    object PasswordsDifferentError : SignUpError()
    object PasswordEmptyError : SignUpError()
    object PasswordConfirmEmptyError : SignUpError()
    object NameEmptyError : SignUpError()
    object LoginEmptyError : SignUpError()
    class ExceptionError(val exception: AppError) : SignUpError()
}