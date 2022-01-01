package com.pet.chat.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.ui.RoomMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(val chatProviderImpl: MultipleChatProviderImpl) :
    ViewModel() {
    var curentRoomID = -1
    val messages = MutableStateFlow<List<RoomMessage>>(listOf())
    init {
        viewModelScope.launch {
            val data = chatProviderImpl.chats.asStateFlow().value.firstOrNull { it.roomID == curentRoomID }?.roomMessages ?: listOf()
            messages.emit(data)
        }
    }


}