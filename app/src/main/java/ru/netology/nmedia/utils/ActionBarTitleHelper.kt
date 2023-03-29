package ru.netology.nmedia.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.setActionBarTitle(title: String) {
    (this.activity as AppCompatActivity?)?.supportActionBar?.title = title
}