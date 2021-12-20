package com.pet.chat.network.data.receive

import com.google.gson.annotations.SerializedName
import com.pet.chat.network.data.ui.Chat
import com.pet.chat.network.data.base.Message
import com.pet.chat.network.data.base.Room

data class MessageDelete(
    @SerializedName("chat")
    var chat: Chat,
    @SerializedName("message")
    var message: Message,
    @SerializedName("room")
    var room: Room,
)