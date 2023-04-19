package ru.netology.nmedia.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NMediaApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        //DependencyContainer.initApp(this)
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        //.setMinimumLoggingLevel(android.util.Log.INFO)
        .setWorkerFactory(workerFactory)
        .build()
}
