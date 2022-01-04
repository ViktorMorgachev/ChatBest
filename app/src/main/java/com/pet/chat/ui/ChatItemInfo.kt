package com.pet.chat.ui

import com.pet.chat.ui.screens.chat.RoomMessage

data class ChatItemInfo(
    val roomID: Int,
    val usersIDs: List<Int>,
    var unreadCount: Int,
    var roomMessages: List<RoomMessage> = listOf(),
)
