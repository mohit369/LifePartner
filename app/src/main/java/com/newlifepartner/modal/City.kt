package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class City(
    @SerializedName("city")
    val city: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("images")
    val images: String,
    @SerializedName("state_id")
    val stateId: String
)