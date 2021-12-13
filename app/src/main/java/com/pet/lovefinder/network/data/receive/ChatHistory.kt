package com.pet.lovefinder.network.data.receive

import com.pet.lovefinder.network.data.Chat
import com.pet.lovefinder.network.data.Message
import com.pet.lovefinder.network.data.Room

data class ChatHistory(
    val messages: ArrayList<Message>,
    val chat: Chat?,
    val room: Room?,
)
