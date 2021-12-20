package com.pet.chat.network.data.base

import com.pet.chat.network.data.base.Message
import com.pet.chat.network.data.base.Room
import com.pet.chat.network.data.ui.Chat

data class Dialog(
    val chat: Chat,
    val room: Room,
    val message: Message? = null,
)
