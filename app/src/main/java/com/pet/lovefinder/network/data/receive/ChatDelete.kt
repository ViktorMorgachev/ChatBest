package com.pet.lovefinder.network.data.receive

import com.pet.lovefinder.network.data.Chat
import com.pet.lovefinder.network.data.Room

data class ChatDelete(
    var chat: Chat,
    var room: Room,
)