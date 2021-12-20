package com.pet.chat.network.data.receive

import com.google.gson.annotations.SerializedName
import com.pet.chat.network.data.ui.Chat
import com.pet.chat.network.data.base.Room

data class ChatRead(
    @SerializedName("chat")
    var chat: Chat,
    @SerializedName("room")
    var room: Room,
)