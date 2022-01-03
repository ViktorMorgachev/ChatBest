package com.pet.chat.network.data

import com.pet.chat.network.data.receive.ChatHistory
import com.pet.chat.ui.ChatItemInfo
import com.pet.chat.ui.screens.chat.toSimpleMessage

fun ChatHistory.toChatItemInfo(): ChatItemInfo? {
    return if (this.room != null && this.chat != null) {
        val userIds: MutableList<Int> = mutableListOf()
        var roomID = room.id
        room.users.forEach {
            userIds.add(it.id.toInt())
        }
        val unreadCount = this.chat.unread_count
        ChatItemInfo(roomID = roomID.toInt(),
            usersIDs = userIds,
            unreadCount = unreadCount.toInt(),
            roomMessages = this.messages.map { it.toSimpleMessage() }.toMutableList())
    } else null


}