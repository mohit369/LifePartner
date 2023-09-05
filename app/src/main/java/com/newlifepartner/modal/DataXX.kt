package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DataXX(
    @SerializedName("profile")
    val profile: Profile
):Serializable