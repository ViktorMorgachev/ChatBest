package com.pet.lovefinder.network.data

data class Room(
    val id: Number,
    val users: Array<User>,
    val created_at: String,
)
