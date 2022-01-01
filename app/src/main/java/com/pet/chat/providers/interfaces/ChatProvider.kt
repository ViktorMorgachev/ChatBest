package com.pet.chat.providers.interfaces

import kotlinx.coroutines.flow.MutableStateFlow

interface ChatProvider<T> {
    val chats: MutableStateFlow<List<T>>
    fun deleteChat(chatID: Int)
    fun createChat(chat: T)
    fun clearChat(chatID: Int)
    fun updateChat(chat: T)
    fun updateChatState(data: Any)
}