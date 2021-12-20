package com.pet.chat.network.data.base

data class Room(
    val id: Number,
    val users: ArrayList<User>,
    val created_at: String,
)
