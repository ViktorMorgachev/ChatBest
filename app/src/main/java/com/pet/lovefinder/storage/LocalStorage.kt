package com.pet.lovefinder.storage

import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.network.data.base.Messages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

// TODO После настроить структуру для комнаты с привязкой id
object LocalStorage {
    val chats = MutableStateFlow<List<ChatDetails>>(emptyList())
    val messages = MutableStateFlow<List<Messages>>(emptyList())

    fun updateChats(chatDetails: ChatDetails) = runBlocking {
        if (!chats.value.contains(chatDetails))
            chats.emit(chats.value.plus(chatDetails))
    }

    fun updateMessages(newMessages: List<Messages>) = runBlocking {
        val newValue = messages.value.toMutableList()
        newValue.forEach { message ->
            if (!newMessages.contains(message)) {
                newValue.add(message)
            }
        }
        messages.emit(newValue)
    }
}