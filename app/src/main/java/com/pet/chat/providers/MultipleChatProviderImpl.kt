package com.pet.chat.providers

import android.util.Log
import com.pet.chat.helpers.addLast
import com.pet.chat.helpers.removeWithInstance
import com.pet.chat.helpers.replaceWithInstance
import com.pet.chat.network.data.receive.ChatRead
import com.pet.chat.providers.interfaces.ChatProvider
import com.pet.chat.providers.interfaces.MultipleMessagesProvider
import com.pet.chat.ui.screens.chat.RoomMessage
import com.pet.chat.ui.screens.chat.State
import com.pet.chat.ui.ChatItemInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MultipleChatProviderImpl @Inject constructor(override val chats: MutableStateFlow<List<ChatItemInfo>>) : ChatProvider<ChatItemInfo>, MultipleMessagesProvider<RoomMessage> {

    init {
        Log.d("ChatProviderImpl", "Init")
    }

    override fun deleteChat(chatID: Int) {
        val actualChat = getCurrentChat(roomID = chatID)
        if (actualChat != null) {
            chats.value = chats.value.removeWithInstance(actualChat)
        }
        Log.d("ChatProviderImpl", "deleteChat Chats ${chats.value}")
    }

    override fun createChat(chat: ChatItemInfo) {
        chats.value = chats.value.plus(chat)
    }

    override fun clearChat(chatID: Int) {
        val actualChat = getCurrentChat(roomID = chatID)
        if (actualChat != null) {
            actualChat.roomMessages = listOf()
        }
        Log.d("ChatProviderImpl", "clearChat Chats ${chats.value}")
    }

    override fun updateChat(chat: ChatItemInfo) {
        val actualChat = getCurrentChat(roomID = chat.roomID)
        if (actualChat != null) {
            val newList = actualChat.roomMessages.plus(chat.roomMessages)
            actualChat.roomMessages = newList
        } else {
            chats.value = chats.value.addLast(chat)
        }
        Log.d("ChatProviderImpl", "updateChat Chats ${chats.value}")

    }

    override fun addMessage(message: RoomMessage, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages = currentChat.roomMessages.addLast(message)
        }
        Log.d("ChatProviderImpl", "addMessage Chats ${chats.value}")
    }

    override fun deleteMessageByID(messageID: Int, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages.firstOrNull { it.messageID == messageID }?.let {
                currentChat.roomMessages = currentChat.roomMessages.removeWithInstance(it)
            }
        }
    }

    override fun addTempMessage(data: RoomMessage, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages = currentChat.roomMessages.addLast(data)
        }
        Log.d("ChatProviderImpl", "addTempMessage Chats ${chats.value}")
    }

    private fun getCurrentChat(roomID: Int): ChatItemInfo? {
        return chats.value.firstOrNull { it.roomID == roomID }
    }


    override fun updateChatState(data: Any) {
        if (data is ChatRead){
            chats.value.find { it.roomID == data.room.id.toInt() }?.let {
                it.unreadCount = 0
            }
        }
    }

    override fun fileUploadError(messageID: Int, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages.firstOrNull { it.messageID == messageID }?.let { roomMessage ->
                roomMessage.file?.state = State.Error
                currentChat.roomMessages = currentChat.roomMessages.replaceWithInstance(roomMessage, (roomMessage as RoomMessage.SendingMessage).copy())
            }
        }
    }


}