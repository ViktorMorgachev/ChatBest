package com.pet.chat.ui.screens.autorization

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.ViewState
import com.pet.chat.providers.interfaces.EventFromServerProvider
import com.pet.chat.providers.interfaces.ViewStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

val tag = "AutorizationViewModel"

@HiltViewModel
class AutorizationViewModel @Inject constructor(
    val connectionManager: ConnectionManager,
    val viewStateProvider: ViewStateProvider,
    val eventFromServerProvider: EventFromServerProvider
) : ViewModel() {


    var lastAction = {
    }

    init {
        Log.d(tag, "Init")
        viewModelScope.launch(Dispatchers.IO) {
            eventFromServerProvider.events.collect {
                if (isActive){
                    Log.d(tag, "EventFromServer $it")
                    reduce(it)
                }

            }
        }
    }

    private fun reduce(eventFromServer: EventFromServer) {
        when (eventFromServer) {
            is EventFromServer.AutorizationEvent -> {
                viewStateProvider.postViewState(ViewState.Success())
            }
            is EventFromServer.ConnectionError -> {
                viewStateProvider.postViewState(ViewState.Error(eventFromServer.data))
            }
        }
    }

    fun authorize(authEvent: EventToServer.AuthEvent) = viewModelScope.launch(Dispatchers.IO) {
        lastAction = {
            connectionManager.postEventToServer(
                authEvent,
                error = { viewStateProvider.postViewState(ViewState.Error(it)) })
        }
    }

    fun tryToConnect() = viewModelScope.launch(Dispatchers.IO){
        connectionManager.tryToConnect()
    }

    fun dismiss(){
        Log.d("MessagesViewModel", "dismiss()")
        viewModelScope.cancel()
    }
}