package com.pet.chat.network.data.receive

import com.pet.chat.network.data.Chat
import com.pet.chat.network.data.Message
import com.pet.chat.network.data.Room

data class ChatHistory(
    val messages: ArrayList<Message>,
    val chat: Chat?,
    val room: Room?,
)
