package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DataXXX(
    @SerializedName("vendors")
    val vendors: Vendors
)