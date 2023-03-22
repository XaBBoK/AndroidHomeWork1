package ru.netology.nmedia.error

import java.io.IOException

sealed class AppError(var code: String) : IOException()
class ApiAppError(val status: Int, code: String) : AppError(code)
object NetworkAppError : AppError("error_network")
object UnknownAppError : AppError("error_unknown")