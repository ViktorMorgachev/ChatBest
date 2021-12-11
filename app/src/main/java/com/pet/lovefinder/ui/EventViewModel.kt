package com.pet.lovefinder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.lovefinder.network.ConnectionManager
import com.pet.lovefinder.network.Event
import com.pet.lovefinder.network.Subscriber
import com.pet.lovefinder.network.data.AuthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    val events = MutableStateFlow<Event>(Event.Default("FIRST"))

    fun login(authData: AuthData) {
        ConnectionManager.auth(authData)
    }

    init {
        viewModelScope.launch {
            ConnectionManager.subsribe(object : Subscriber {
                override fun post(event: Event) {
                    viewModelScope.launch {
                        events.emit(event)
                    }
                }
            })
        }

    }

}