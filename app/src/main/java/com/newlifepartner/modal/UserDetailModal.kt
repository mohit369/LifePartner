package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UserDetailModal(
    @SerializedName("data")
    val `data`: DataXX,
    @SerializedName("status")
    val status: Boolean
)