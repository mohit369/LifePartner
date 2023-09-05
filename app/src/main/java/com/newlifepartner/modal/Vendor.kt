package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Vendor(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("package")
    val _package: String,
    @SerializedName("vendor_photo")
    val vendorPhoto: String?
)