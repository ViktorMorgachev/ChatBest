package com.pet.chat.ui.screens.chats

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.ViewState
import com.pet.chat.network.data.send.ChatDelete
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.providers.interfaces.ViewStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    val chatProviderImpl: MultipleChatProviderImpl,
    val viewStateProvider: ViewStateProvider,
    val connectionManager: ConnectionManager
) : ViewModel() {

    init {
        Log.d("ChatsViewModel", "Init")
        viewModelScope.launch(Dispatchers.IO) {
            if (isActive) {
                chatProviderImpl.chats.collect {
                    if (isActive) {
                        Log.d("ChatsViewModel", "ChatItemsInfo $it")
                        if (it.isEmpty()) {
                            viewStateProvider.postViewState(ViewState.StateNoItems)
                        } else
                            viewStateProvider.postViewState(ViewState.Display(listOf(it)))
                    }
                }
            }

        }
    }

    fun deleteChat(chatDelete: ChatDelete) = viewModelScope.launch(Dispatchers.IO) {
        connectionManager.postEventToServer(EventToServer.DeleteChat(chatDelete)) {
         //   viewStateProvider.postViewState(ViewState.Error(it))
        }
    }

    fun dismiss(){
        Log.d("ChatViewModel", "dismiss()")
        viewModelScope.cancel()
    }

}