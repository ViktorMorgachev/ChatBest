package com.pet.lovefinder.ui

import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.network.data.receive.ChatDelete
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

// TODO После настроить структуру для комнаты с привязкой id
object ViewDataStorage {
    val chats = MutableStateFlow<MutableList<ChatItemInfo>>(mutableListOf())

    fun updateChat(chatDetails: ChatItemInfo) = runBlocking {
        val lastListMessagesInfo = chats.value.find { it.roomID == chatDetails.roomID }
        if (lastListMessagesInfo != null) {
            val newList = lastListMessagesInfo.roomMessages.plus(chatDetails.roomMessages)
            lastListMessagesInfo.roomMessages = newList
        } else chats.value.add(chatDetails)
        chats.emit(chats.value)
    }

    // TODO обязательно
    fun deleteMessage() {

    }

    fun updateChat(chatsDetails: List<ChatItemInfo>) = runBlocking {
        chatsDetails.forEach { chatDetail ->
            val lastListMessagesInfo = chats.value.find { it.roomID == chatDetail.roomID }
            if (lastListMessagesInfo != null) {
                val newList = lastListMessagesInfo.roomMessages.plus(chatDetail.roomMessages)
                lastListMessagesInfo.roomMessages = newList
            } else chats.value.add(chatDetail)
        }
        chats.emit(chats.value)
    }

    // Готово
    fun deleteChat(chatDetails: ChatDelete) = runBlocking {
        chats.value.firstOrNull { it.roomID == chatDetails.room.id.toInt() }?.let {
            chats.value.remove(it)
        }
        chats.emit(chats.value)
    }
}