package ru.netology.nmedia.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

tailrec fun Context.getActivity(): AppCompatActivity {
    return this as? AppCompatActivity
        ?: (this as ContextWrapper).baseContext.getActivity()
}