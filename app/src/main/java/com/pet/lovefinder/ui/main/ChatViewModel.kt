package com.pet.lovefinder.ui.main

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.lovefinder.App
import com.pet.lovefinder.network.ConnectionManager
import com.pet.lovefinder.network.EventFromServer
import com.pet.lovefinder.network.Subscriber
import com.pet.lovefinder.network.data.Chat
import com.pet.lovefinder.network.data.Room
import com.pet.lovefinder.network.data.send.*
import com.pet.lovefinder.storage.Prefs
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