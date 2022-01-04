package com.pet.chat.providers

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
import javax.inject.Inject


// Возможно придётся обьеденить ChatProvider
class MultipleChatProviderImpl @Inject constructor(override val chats: MutableStateFlow<List<ChatItemInfo>>) : ChatProvider<ChatItemInfo>, MultipleMessagesProvider<RoomMessage> {

    override fun deleteChat(chatID: Int) {
        val actualChat = getCurrentChat(roomID = chatID)
        if (actualChat != null) {
            chats.value = chats.value.removeWithInstance(actualChat)
        }
    }

    override fun createChat(chat: ChatItemInfo) {
        chats.value = chats.value.plus(chat)
    }

    override fun clearChat(chatID: Int) {
        val actualChat = getCurrentChat(roomID = chatID)
        if (actualChat != null) {
            actualChat.roomMessages = listOf()
        }
    }

    override fun updateChat(chat: ChatItemInfo) {
        val actualChat = getCurrentChat(roomID = chat.roomID)
        if (actualChat != null) {
            val newList = actualChat.roomMessages.plus(chat.roomMessages)
            actualChat.roomMessages = newList
        } else {
            chats.value = chats.value.addLast(chat)
        }
    }

    override fun addMessage(message: RoomMessage, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages = currentChat.roomMessages.addLast(message)
        }
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