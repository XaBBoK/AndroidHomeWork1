package ru.netology.nmedia.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject

@HiltWorker
class SendPushTokenWorker @AssistedInject constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters
) :
    CoroutineWorker(context, parameters) {
    companion object {
        const val TOKEN_KEY = "TOKEN"
        const val NAME = "SendPushTokenWorker"
    }

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    override suspend fun doWork(): Result =
        try {
            val token =
                inputData.getString(TOKEN_KEY) ?: firebaseMessaging.token.await()
            val t = PushToken(token)
            val tt = firebaseMessaging.token.await()
            println(tt)
            apiService.savePushToken(t)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
}
