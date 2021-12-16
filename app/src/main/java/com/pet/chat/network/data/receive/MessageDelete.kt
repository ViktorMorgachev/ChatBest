package com.pet.chat.network.data.receive

import com.google.gson.annotations.SerializedName
import com.pet.chat.network.data.Chat
import com.pet.chat.network.data.Message
import com.pet.chat.network.data.Room

data class MessageDelete(
    @SerializedName("chat")
    var chat: Chat,
    @SerializedName("message")
    var message: Message,
    @SerializedName("room")
    var room: Room,
)