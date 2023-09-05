package com.newlifepartner.modal

data class ChatModal(
    val created_at: String,
    val from_user: String,
    val from_user_type: String,
    val id: String,
    val message: String,
    val status: String,
    val to_user: String,
    val to_user_type: String,
    val type: String,
    val updated_at: String
)