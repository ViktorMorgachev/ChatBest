package com.pet.lovefinder.network.data.receive

import com.google.gson.annotations.SerializedName
import com.pet.lovefinder.network.data.Chat
import com.pet.lovefinder.network.data.Message
import com.pet.lovefinder.network.data.Room

data class MessageDelete(
    @SerializedName("chat")
    var chat: Chat,
    @SerializedName("message")
    var message: Message,
    @SerializedName("room")
    var room: Room,
)