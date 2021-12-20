package com.pet.chat.network.data.receive

import com.pet.chat.network.data.ui.Chat
import com.pet.chat.network.data.base.Room

data class ChatDelete(
    var chat: Chat,
    var room: Room,
)