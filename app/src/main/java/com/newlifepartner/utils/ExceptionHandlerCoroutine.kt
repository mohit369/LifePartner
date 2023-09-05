package com.newlifepartner.utils

import kotlinx.coroutines.CoroutineExceptionHandler

object ExceptionHandlerCoroutine{
    val handler = CoroutineExceptionHandler{
        _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }
}