package com.pet.chat.providers.interfaces

import com.pet.chat.network.EventFromServer
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface EventFromServerProvider {
    val events :  MutableStateFlow<EventFromServer>
    fun postEventFromServer(eventFromServer: EventFromServer)
}