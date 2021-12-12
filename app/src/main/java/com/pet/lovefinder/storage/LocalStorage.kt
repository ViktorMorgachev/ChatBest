package com.pet.lovefinder.storage

import com.pet.lovefinder.network.data.Message
import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.network.data.base.Messages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

// TODO После настроить структуру для комнаты с привязкой id
object LocalStorage {
    val chats = MutableStateFlow<MutableList<ChatDetails>>(mutableListOf())
    val messages = MutableStateFlow<List<Message>>(emptyList())

    fun updateChats(chatDetails: ChatDetails) = runBlocking {
        if (!chats.value.map { it.roomID }.contains(chatDetails.roomID))
            chats.emit(chats.value.plus(chatDetails).toMutableList())
    }

    fun updateChats(chatsDetails: List<ChatDetails>) = runBlocking {
        chats.value = arrayListOf()
        chats.emit(chatsDetails.toMutableList())
    }

    fun deleteChat(chatDetails: ChatDetails) = runBlocking {
        chats.value.remove(chatDetails)
    }

    fun updateMessages(newMessages: List<Message>) = runBlocking {
        messages.value = arrayListOf()
        messages.emit(messages.value.plus(newMessages))
    }
}