package ru.netology.nmedia.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.dto.PushToken

class SendPushTokenWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    companion object {
        const val TOKEN_KEY = "TOKEN"
        const val NAME = "SendPushTokenWorker"
    }

    override suspend fun doWork(): Result =
        try {
            val token =
                inputData.getString(TOKEN_KEY) ?: FirebaseMessaging.getInstance().token.await()
            Api.service.savePushToken(PushToken(token))
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
}