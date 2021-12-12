package com.pet.lovefinder.network.data.receive

import com.pet.lovefinder.network.data.Chat
import com.pet.lovefinder.network.data.Message
import com.pet.lovefinder.network.data.Room

data class MessageNew(
    val chat: Chat,
    val room: Room,
    val message: Message)
