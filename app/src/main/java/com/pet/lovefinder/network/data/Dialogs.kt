package com.pet.lovefinder.network.data

data class Dialog(
    val chat: Chat,
    val room: Room,
    val message: Message? = null,
)
