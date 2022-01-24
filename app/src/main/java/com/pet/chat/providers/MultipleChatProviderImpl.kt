package com.pet.chat.providers

import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pet.chat.helpers.addAll
import com.pet.chat.helpers.addLast
import com.pet.chat.helpers.removeWithInstance
import com.pet.chat.helpers.replaceWithInstance
import com.pet.chat.network.data.receive.ChatRead
import com.pet.chat.providers.interfaces.ChatProvider
import com.pet.chat.providers.interfaces.MultipleMessagesProvider
import com.pet.chat.ui.screens.chat.RoomMessage
import com.pet.chat.ui.screens.chat.State
import com.pet.chat.ui.ChatItemInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
        update()
    }

    override fun clearChat(chatID: Int) {
        val actualChat = getCurrentChat(roomID = chatID)
        if (actualChat != null) {
            actualChat.roomMessages = listOf()
        }
        update()
        Log.d("ChatProviderImpl", "clearChat Messages ${actualChat?.roomMessages} Size ${actualChat?.roomMessages?.size}")
    }

    private fun update(){
        GlobalScope.launch(Dispatchers.IO) {
            chats.emit(listOf<ChatItemInfo>().addAll(chats.value))
        }
    }

    override fun updateChat(chat: ChatItemInfo) {
        var actualChat = getCurrentChat(roomID = chat.roomID)
        if (actualChat != null) {
            chat.roomMessages.forEach { roomMessage ->
                if (!actualChat!!.roomMessages.map { it.messageID }.contains(roomMessage.messageID)){
                    actualChat!!.roomMessages = actualChat!!.roomMessages.addLast(roomMessage)
                    actualChat = actualChat!!.copy()
                }
            }

        } else {
            chats.value = chats.value.addLast(chat)
        }
        chats.value.forEach {
            Log.d("ChatProviderImpl", "Chat: ${it.roomID} Messages: ${it.roomMessages.size}")
        }
        update()

    }

    override fun addMessage(message: RoomMessage, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages = currentChat.roomMessages.addLast(message)
        }
        Log.d("ChatProviderImpl", "addMessage Messages ${currentChat?.roomMessages} Size ${currentChat?.roomMessages?.size}")
    }

    override fun deleteMessageByID(messageID: Int, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages.firstOrNull { it.messageID == messageID }?.let {
                currentChat.roomMessages = currentChat.roomMessages.removeWithInstance(it)
            }
        }
        Log.d("ChatProviderImpl", "deleteMessageByID Messages ${currentChat?.roomMessages} Size ${currentChat?.roomMessages?.size}")
    }

    override fun addTempMessage(data: RoomMessage, roomID: Int) {
        val currentChat = getCurrentChat(roomID = roomID)
        if (currentChat != null) {
            currentChat.roomMessages = currentChat.roomMessages.addLast(data)
        }
        Log.d("ChatProviderImpl", "addTempMessage Messages ${currentChat?.roomMessages} Size ${currentChat?.roomMessages?.size}")
    }

    private fun getCurrentChat(roomID: Int): ChatItemInfo? {
        return chats.value.firstOrNull { it.roomID == roomID }
    }


    override fun updateChatState(data: Any) {
        if (data is ChatRead){
            if (data.room.id == null) return
            chats.value.find { it.roomID == data.room.id!!.toInt() }?.let {
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