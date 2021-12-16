package com.pet.chat.ui.main

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.Subscriber
import com.pet.chat.network.data.send.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    val events = MutableStateFlow<EventFromServer>(EventFromServer.NO_INITIALIZED)


    fun postEventToServer(eventToServer: EventToServer) {
        ConnectionManager.postEventToServer(event = eventToServer, error = {
            val resultText = App.instance.applicationContext.getText(R.string.something_went_wrong).toString() + it
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

}