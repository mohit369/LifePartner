package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName

data class VerifyOtpModal(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
) {
    data class Data(
        @SerializedName("email")
        val email: String,
        @SerializedName("first_name")
        val firstName: Any,
        @SerializedName("id")
        val id: String,
        @SerializedName("last_name")
        val lastName: Any,
        @SerializedName("mobile_no")
        val mobileNo: String
    )
}