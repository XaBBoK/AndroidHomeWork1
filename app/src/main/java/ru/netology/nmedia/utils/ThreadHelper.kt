package ru.netology.nmedia.utils

import android.annotation.SuppressLint
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Looper
import kotlin.concurrent.thread


@SuppressLint("ObsoleteSdkInt")
fun <T> ensureNotOnMainThread(block: () -> T) {
    if (if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread) {
        thread {
            block()
        }
        return
    }

    block()
}