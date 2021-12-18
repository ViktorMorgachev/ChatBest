package com.pet.chat.ui.main

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.App
import com.pet.chat.BuildConfig
import com.pet.chat.R
import com.pet.chat.events.InternalEvent
import com.pet.chat.helpers.ImageUtils
import com.pet.chat.helpers.addAll
import com.pet.chat.helpers.addLast
import com.pet.chat.helpers.removeWithInstance
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.Subscriber
import com.pet.chat.network.data.User
import com.pet.chat.network.data.receive.*
import com.pet.chat.network.data.receive.ChatDelete
import com.pet.chat.network.data.receive.ChatRead
import com.pet.chat.ui.ChatItemInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    val events = MutableStateFlow<EventFromServer>(EventFromServer.NO_INITIALIZED)
    val chats = MutableStateFlow<List<ChatItemInfo>>(listOf())
    val users = MutableStateFlow<List<User>>(mutableListOf())
    private var imageUri: Uri? = null
    val internalEvents = MutableStateFlow<InternalEvent?>(null)

    fun postEventToServer(eventToServer: EventToServer) {
        Log.d("EventToServer", "$eventToServer")
        ConnectionManager.postEventToServer(event = eventToServer, error = {
            val resultText = App.instance.applicationContext.getText(R.string.something_went_wrong)
                .toString() + it
            Toast.makeText(App.instance.applicationContext, resultText, Toast.LENGTH_LONG).show()
        })
    }

    init {
        viewModelScope.launch {
            ConnectionManager.subsribe(object : Subscriber {
                override fun post(eventFromServer: EventFromServer) {
                    viewModelScope.launch {
                        events.emit(eventFromServer)
                    }
                }
            })
        }

    }

    fun postInternalAction(internalState: InternalEvent) = viewModelScope.launch(Dispatchers.IO) {
        internalEvents.emit(internalState)
    }

    fun updateChat(chatDetails: ChatItemInfo) = viewModelScope.launch(Dispatchers.IO) {
        val actualChat = chats.value.find { it.roomID == chatDetails.roomID }
        if (actualChat != null) {
            val newList = actualChat.roomMessages.plus(chatDetails.roomMessages)
            actualChat.roomMessages = newList
        } else {
            chats.value = chats.value.addLast(chatDetails)
        }
        updateChat()
    }

    fun deleteMessage(messageDelete: MessageDelete) = viewModelScope.launch(Dispatchers.IO) {
        val currentChat = chats.value.first { messageDelete.room.id.toInt() == it.roomID }
        var messages = currentChat.roomMessages
        messages.firstOrNull { it.messageID == messageDelete.message.id.toInt() }?.let {
            messages = messages.removeWithInstance(it)
        }
        currentChat.roomMessages = messages
        updateChat()
    }

    fun clearChat(chatClear: ChatClear) = viewModelScope.launch {
        chats.value.find { it.roomID == chatClear.room.id.toInt() }?.let {
            chats.value = chats.value.removeWithInstance(it)
            updateChat()
        }
    }

    fun updateChat(chatsDetails: List<ChatItemInfo>) = viewModelScope.launch(Dispatchers.IO) {
        chatsDetails.forEach { chatDetail ->
            val lastListMessagesInfo = chats.value.find { it.roomID == chatDetail.roomID }
            if (lastListMessagesInfo != null) {
                lastListMessagesInfo.roomMessages = lastListMessagesInfo.roomMessages.addAll(chatDetail.roomMessages)
            } else chats.value = chats.value.addLast(chatDetail)
        }
        updateChat()
    }

    suspend fun updateChat() {
        chats.emit(chats.value)
        Log.d("UpdateChat", "Chat: ${chats.value}")
    }

    fun deleteChat(chatDetails: ChatDelete) = viewModelScope.launch(Dispatchers.IO) {
        chats.value.firstOrNull { it.roomID == chatDetails.room.id.toInt() }?.let {
            chats.value = chats.value.removeWithInstance(it)
        }
        updateChat()
    }

    fun updateChatState(data: ChatRead) = viewModelScope.launch {
        chats.value.find { it.roomID == data.room.id.toInt() }?.let {
            it.unreadCount = 0
        }
        updateChat()
    }

    fun updateUserStatus(data: UserOnline) {

    }

    fun takePicture(context: Context, launchCamera: (Uri) -> Unit) =
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                ImageUtils.createImageFile(context)?.also { file ->
                    imageUri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file)
                }
            }.join()
            imageUri?.let { launchCamera(it) }
        }
}

