package com.pet.chat.network.data.receive

import com.pet.chat.network.data.ui.Chat
import com.pet.chat.network.data.base.Message
import com.pet.chat.network.data.base.Room

data class MessageNew(
    val chat: Chat,
    val room: Room,
    val message: Message
)
