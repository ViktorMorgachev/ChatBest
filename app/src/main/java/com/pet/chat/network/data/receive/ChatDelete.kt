package com.pet.chat.network.data.receive

import com.pet.chat.network.data.Chat
import com.pet.chat.network.data.Room

data class ChatDelete(
    var chat: Chat,
    var room: Room,
)