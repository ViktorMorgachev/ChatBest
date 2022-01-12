package com.pet.chat.network.data

import com.pet.chat.network.data.receive.ChatHistory
import com.pet.chat.ui.ChatItemInfo
import com.pet.chat.ui.screens.chat.toSimpleMessage

fun ChatHistory.toChatItemInfo(): ChatItemInfo {

    return run {
        val userIds: MutableList<Int> = mutableListOf()
        val roomID = room.id!!.toInt()
        room.users.forEach {
            userIds.add(it.id.toInt())
        }
        val unreadCount = this.chat.unread_count
        ChatItemInfo(roomID = roomID,
            usersIDs = userIds,
            unreadCount = unreadCount.toInt(),
            roomMessages = this.messages.map { it.toSimpleMessage() }.toMutableList())
    }


}