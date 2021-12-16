package com.pet.chat.network.data.receive

import com.google.gson.annotations.SerializedName
import com.pet.chat.network.data.Chat
import com.pet.chat.network.data.Room

data class ChatRead(
    @SerializedName("chat")
    var chat: Chat,
    @SerializedName("room")
    var room: Room,
)