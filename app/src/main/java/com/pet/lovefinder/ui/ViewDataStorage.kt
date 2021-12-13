package com.pet.lovefinder.ui

import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.network.data.receive.ChatDelete
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

// TODO После настроить структуру для комнаты с привязкой id
object ViewDataStorage {
    val chats = MutableStateFlow<MutableList<ChatItemInfo>>(mutableListOf())

    fun updateChat(chatDetails: ChatItemInfo) = runBlocking {
        if (chats.value.contains(chatDetails)) {
            chats.value.firstOrNull { it.roomID == chatDetails.roomID }?.let {
                it.roomMessages.plus(chatDetails)
            }
        } else {
            chats.value.add(chatDetails)
        }
    }

    // TODO обязательно
    fun deleteMessage() {

    }

    fun updateChat(chatsDetails: List<ChatItemInfo>) = runBlocking {
        chatsDetails.forEach { chatDetail ->
            if (chats.value.contains(chatDetail)) {
                chats.value.firstOrNull { it.roomID == chatDetail.roomID }?.let {
                    it.roomMessages.plus(chatDetail)
                }
            } else {
                chats.value.add(chatDetail)
            }
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