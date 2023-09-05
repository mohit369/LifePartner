package com.newlifepartner.modal

data class ResponseDashboard<T>(
    val `data`: List<T>,
    val status: Boolean
)