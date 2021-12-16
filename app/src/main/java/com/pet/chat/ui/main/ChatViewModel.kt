package com.pet.chat.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.App
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.Subscriber
import com.pet.chat.network.data.send.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {

    val events = MutableStateFlow<EventFromServer>(EventFromServer.Default("FIRST"))

    fun login(userAuth: UserAuth) {
        ConnectionManager.auth(userAuth)
        App.prefs?.saveUser(userAuth)
    }

    fun createChat(chatStart: ChatStart) {
        ConnectionManager.createChat(chatStart)
    }

    fun getChatHistory(chatHistory: ChatHistory) {
        ConnectionManager.getChatHistory(chatHistory)
    }

    fun deleteChat(chatDelete: ChatDelete) {
        ConnectionManager.deleteChat(chatDelete)
    }

    fun sendMesage(sendMessage: SendMessage) {
        ConnectionManager.sendMesages(sendMessage = sendMessage)
    }

    fun clearChat(roomID: Int) {
        ConnectionManager.clearChat(roomID = roomID)
    }

    fun deleteMessage(deleteMessage: DeleteMessage) {
        ConnectionManager.deleteMessage(deleteMessage = deleteMessage)
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

}