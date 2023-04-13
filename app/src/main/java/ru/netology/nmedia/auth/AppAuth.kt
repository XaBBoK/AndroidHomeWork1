package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.presentation.AuthModel
import ru.netology.nmedia.workers.SendPushTokenWorker
import java.lang.ref.WeakReference

class AppAuth private constructor(contextParam: Context) {
    private var _context: WeakReference<Context> = WeakReference(contextParam)
    private val context: Context
        get() = requireNotNull(_context.get())

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _data: MutableStateFlow<AuthModel?>

    val data: StateFlow<AuthModel?>
        get() = _data.asStateFlow()

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        if (token == null || id == 0L) {
            _data = MutableStateFlow(null)

            prefs.edit { clear() }
        } else {
            _data = MutableStateFlow(
                AuthModel(id = id, token = token)
            )
        }

        sendPushToken(token)
    }

    fun sendPushToken(token: String? = null) {
        val request = OneTimeWorkRequestBuilder<SendPushTokenWorker>()
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setInputData(
                Data.Builder()
                    .putString(SendPushTokenWorker.TOKEN_KEY, token)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(SendPushTokenWorker.NAME, ExistingWorkPolicy.REPLACE, request)
            .enqueue()
    }

    fun isAuth(): Boolean {
        synchronized(this) {
            val token = prefs.getString(TOKEN_KEY, null)
            val id = prefs.getLong(ID_KEY, 0L)
            return !(token == null || id == 0L)
        }
    }

    fun setAuth(id: Long, token: String) {
        synchronized(this) {
            _data.value = AuthModel(id = id, token = token)
            prefs.edit {
                putLong(ID_KEY, id)
                putString(TOKEN_KEY, token)
            }
            sendPushToken()
        }
    }

    @Synchronized
    fun removeAuth() {
        synchronized(this) {
            _data.value = null
            prefs.edit { clear() }
            sendPushToken()
        }
    }

    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val ID_KEY = "ID_KEY"

        @Volatile
        private var INSTANCE: AppAuth? = null

        fun getInstance(): AppAuth = synchronized(this) {
            requireNotNull(INSTANCE) {
                "You must call 'init(context: Context)' before 'getInstance'"
            }
        }

        fun init(context: Context): AppAuth = synchronized(this) {
            INSTANCE ?: AppAuth(context).apply {
                INSTANCE = this
            }
        }

    }
}