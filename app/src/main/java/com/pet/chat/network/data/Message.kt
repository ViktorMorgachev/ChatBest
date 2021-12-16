package com.pet.chat.network.data

data class Message(
    val id: Number,
    val room_id: Number,
    val user_id: Number,
    val created_at: String?,
    val updated_at: String?,
    val text: String,
    val attachment: Attachment? = null,
)
