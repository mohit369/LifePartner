package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("entity_id")
        val entityId: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("user_type")
        val userType: String
    )
}