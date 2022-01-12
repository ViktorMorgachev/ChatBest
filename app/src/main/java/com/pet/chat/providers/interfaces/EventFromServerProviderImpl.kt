package com.pet.chat.providers.interfaces

import android.util.Log
import com.pet.chat.network.EventFromServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton


class EventFromServerProviderImpl @Inject constructor(override val events: MutableStateFlow<EventFromServer>) : EventFromServerProvider {
    override fun postEventFromServer(eventFromServer: EventFromServer) {
        runBlocking(Dispatchers.IO) {
            Log.d("EventFromServerProvider", "EventFromServer $eventFromServer")
            events.emit(eventFromServer)
        }
    }
}