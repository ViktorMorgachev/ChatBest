package com.pet.chat.ui.screens.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.base.File
import com.pet.chat.network.data.send.ChatDelete
import com.pet.chat.network.data.send.SendMessage
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.providers.ViewStateProviderImpl
import com.pet.chat.providers.interfaces.EventFromServerProviderImpl
import com.pet.chat.providers.interfaces.ViewState
import com.pet.chat.ui.screens.chat.RoomMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatsViewModel @Inject constructor(
    val chatProviderImpl: MultipleChatProviderImpl,
    val viewStateProvider: ViewStateProviderImpl,
    val eventFromServerProvider: EventFromServerProviderImpl,
    val connectionManager: ConnectionManager
) : ViewModel() {

    val chats = MutableStateFlow<List<ChatItemInfo>>(listOf())

    init {

        viewModelScope.launch(Dispatchers.IO) {
            eventFromServerProvider.events.collect {
                reduce(it)
            }
            val data = chatProviderImpl.chats.asStateFlow().value
            chats.emit(data)
        }
    }

    fun deleteChat(chatDelete: ChatDelete) = viewModelScope.launch(Dispatchers.IO) {
        connectionManager.postEventToServer(EventToServer.DeleteChat(chatDelete)) {
         //   viewStateProvider.postViewState(ViewState.Error(it))
        }
    }

    private fun reduce(eventFromServer: EventFromServer) {
        when (eventFromServer) {
            is EventFromServer.ChatDeleteEvent -> {
                viewStateProvider.postViewState(ViewState.Display())
            }
        }
    }


}