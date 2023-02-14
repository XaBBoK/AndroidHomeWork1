package ru.netology.nmedia.utils

inline fun <C> C.ifNotEmpty(defaultValue: () -> C): C where C : CharSequence =
    if (isNotEmpty()) defaultValue() else this