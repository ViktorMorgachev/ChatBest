package com.pet.chat.network.data

data class Room(
    val id: Number,
    val users: ArrayList<User>,
    val created_at: String,
)
