package com.pet.chat.network.data.send

data class ChatStart(
    val user_id: Number,
    val text: String,
    val attachment_id: Number? = null,
)
