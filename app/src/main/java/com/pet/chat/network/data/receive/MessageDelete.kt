package com.pet.chat.network.data.receive

import com.google.gson.annotations.SerializedName
import com.pet.chat.App
import com.pet.chat.network.data.ui.Chat
import com.pet.chat.network.data.base.Message
import com.pet.chat.network.data.base.Room
import com.pet.chat.ui.MainChatModule
import com.pet.chat.ui.screens.chat.RoomMessage

data class MessageDelete(
    @SerializedName("chat")
    var chat: Chat,
    @SerializedName("message")
    var message: Message,
    @SerializedName("room")
    var room: Room,
)

fun MessageDelete.toRoomMessage(): RoomMessage {
    return RoomMessage.SimpleMessage(
        isOwn = MainChatModule.chatsPrefs?.userID == message.user_id.toInt(),
        messageID = message.id.toInt(),
        userID = message.user_id.toString(),
        text = message.text,
        date = message.created_at,
        file = null
    )
}
