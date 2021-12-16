package com.pet.chat.network.data

data class Attachment(
    val id: Number,
    val type: String,
    val file_id: Number,
    val room_id: Number,
    val created_at: String,
)
