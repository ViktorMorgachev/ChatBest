package com.pet.lovefinder.network.data

import com.pet.lovefinder.network.data.receive.ChatHistory
import com.pet.lovefinder.ui.ChatItemInfo
import com.pet.lovefinder.ui.toRoomMessage

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
            roomMessages = this.messages.map { it.toRoomMessage() }.toMutableList())
    } else null


}