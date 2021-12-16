package com.pet.chat.ui

import com.pet.chat.network.data.User
import com.pet.chat.network.data.receive.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

object ViewDataStorage {
    val chats = MutableStateFlow<MutableList<ChatItemInfo>>(mutableListOf())
    val users = MutableStateFlow<MutableList<User>>(mutableListOf())

    fun updateChat(chatDetails: ChatItemInfo) = runBlocking {
        val actualChat = chats.value.find { it.roomID == chatDetails.roomID }
        if (actualChat != null) {
            val newList = actualChat.roomMessages.plus(chatDetails.roomMessages)
            actualChat.roomMessages = newList.toMutableList()
        } else chats.value.add(chatDetails)
        chats.emit(chats.value)
    }

    fun deleteMessage(messageDelete: MessageDelete) = runBlocking {
        val currentChat = chats.value.first { messageDelete.room.id.toInt() == it.roomID }
        val messages = currentChat.roomMessages
        messages.firstOrNull { it.messageID == messageDelete.message.id.toInt() }?.let {
            messages.remove(it)
        }
        chats.emit(chats.value)
    }

    fun clearChat(chatClear: ChatClear) = runBlocking {
        chats.value.find { it.roomID == chatClear.room.id.toInt() }?.let {
            chats.value.remove(it)
            chats.emit(chats.value)
        }
    }

    fun updateChat(chatsDetails: List<ChatItemInfo>) = runBlocking {
        chatsDetails.forEach { chatDetail ->
            val lastListMessagesInfo = chats.value.find { it.roomID == chatDetail.roomID }
            if (lastListMessagesInfo != null) {
                val newList = lastListMessagesInfo.roomMessages.plus(chatDetail.roomMessages)
                lastListMessagesInfo.roomMessages = newList.toMutableList()
            } else chats.value.add(chatDetail)
        }
        chats.emit(chats.value)
    }

    fun deleteChat(chatDetails: ChatDelete) = runBlocking {
        chats.value.firstOrNull { it.roomID == chatDetails.room.id.toInt() }?.let {
            chats.value.remove(it)
        }
        chats.emit(chats.value)
    }

    fun updateChatState(data: ChatRead) = runBlocking {
        chats.value.find { it.roomID == data.room.id.toInt() }?.let {
            it.unreadCount = 0
        }
        chats.emit(chats.value)
    }

    fun updateUserStatus(data: UserOnline) {

    }
}