package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GetVendorDetailModal(
    @SerializedName("data")
    val `data`: DataXXX,
    @SerializedName("status")
    val status: Boolean
)