package com.pet.chat.network.data.receive

import com.pet.chat.network.data.Chat
import com.pet.chat.network.data.Message
import com.pet.chat.network.data.Room

data class MessageNew(
    val chat: Chat,
    val room: Room,
    val message: Message)
