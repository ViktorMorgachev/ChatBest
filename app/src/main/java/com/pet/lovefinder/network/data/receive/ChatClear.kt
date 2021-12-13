package com.pet.lovefinder.network.data.receive

import com.pet.lovefinder.network.data.Chat
import com.pet.lovefinder.network.data.Room

data class ChatClear(
    var chat: Chat,
    var room: Room,
)