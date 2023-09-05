package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Profile(
    @SerializedName("age")
    val age: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("caste")
    val caste: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("hobbies")
    val hobbies: String?,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("interest")
    val interest: String?,
    @SerializedName("mobile_no")
    val mobileNo: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("relationship_status")
    val relationshipStatus: String?,
    @SerializedName("religion")
    val religion: String?,
    @SerializedName("status")
    val status: String
):Serializable

data class Image(
    @SerializedName("user_image")
    val image: String?,
    @SerializedName("id")
    val id: Int
):Serializable