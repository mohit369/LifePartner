package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DataX(
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String?
)