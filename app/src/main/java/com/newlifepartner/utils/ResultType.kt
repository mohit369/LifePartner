package com.newlifepartner.utils

sealed class ResultType<out T> {
    data class Success<out T>(val value: T) : ResultType<T>()
    data class Error(val errorMessage: String) : ResultType<Nothing>()
}

