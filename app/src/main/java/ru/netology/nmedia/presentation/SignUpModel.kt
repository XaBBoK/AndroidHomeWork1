package ru.netology.nmedia.presentation

import java.io.File

data class SignUpModel(
    var name: String,
    var login: String,
    var password: String,
    var password_confirm: String,
    var avatar: File?
)
