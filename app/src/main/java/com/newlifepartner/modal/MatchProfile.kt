package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MatchProfile(
    @SerializedName("age")
    val age: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("hobbies")
    val hobbies: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?
)