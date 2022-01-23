package com.pet.chat.network.data

import com.pet.chat.network.data.base.Dialog
import com.pet.chat.network.data.receive.ChatHistory
import com.pet.chat.network.data.receive.MessageNew
import com.pet.chat.ui.ChatItemInfo
import com.pet.chat.ui.screens.chat.toSimpleMessage

// Delete in future? its bad
var currentRoomID : Int? = null

fun ChatHistory.toChatItemInfo(currentRoomID: Int): ChatItemInfo {

    return run {
        val userIds: MutableList<Int> = mutableListOf()
        val roomID: Int = currentRoomID
        room?.users?.forEach {
            userIds.add(it.id.toInt())
        }
        val unreadCount = this.chat?.unread_count ?: 0
        ChatItemInfo(roomID = roomID,
            usersIDs = userIds,
            unreadCount = unreadCount.toInt(),
            roomMessages = this.messages.map { it.toSimpleMessage() }.toMutableList())
    }
}

fun Dialog.toChatItemInfo(): ChatItemInfo {
    val usersIDs = mutableListOf<Int>()
    this.room.users.forEach {
        usersIDs.add(it.id.toInt())
    }
    val messages = if (this.message != null) listOf(this.message.toSimpleMessage()) else listOf()
    return ChatItemInfo(
        roomID = this.room.id!!.toInt(),
        usersIDs = usersIDs,
        unreadCount = this.chat.unread_count.toInt(),
        roomMessages = messages.toMutableList()
    )
}

fun MessageNew.toChatItemInfo(): ChatItemInfo {
    val usersIDs = mutableListOf<Int>()
    this.room.users.forEach {
        usersIDs.add(it.id.toInt())
    }
    return ChatItemInfo(
        roomID = this.room.id!!.toInt(),
        usersIDs = usersIDs,
        unreadCount = this.chat.unread_count.toInt(),
        roomMessages = mutableListOf(this.message.toSimpleMessage())
    )
}