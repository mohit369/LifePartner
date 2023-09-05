package com.newlifepartner.modal

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseSignUp(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("user_id")
    val userId: String?
)