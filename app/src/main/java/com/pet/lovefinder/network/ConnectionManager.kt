package com.pet.lovefinder.network

import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

sealed class Event {
    data class DeleteMessage(val messageID: String) : Event()
    data class MessageSend(
        val room_id: Number,
        val text: String,
        val attachment_id: Int?,
    ) : Event()

    data class Default(val Data: Any?) : Event()

}

object ConnectionManager {

    private var socket: Socket? = null
    val events = MutableStateFlow<Event>(Event.Default(null))

    fun connectionActive(): Boolean {
        return socket?.isActive == true
    }

    fun initConnection(uri: String, options: IO.Options) {
        try {
            socket = IO.socket(uri, options)
            registratingEvents()
        } catch (error: Throwable) {
            error.printStackTrace()
        }
    }

    private fun registratingEvents() = runBlocking {
        socket?.let { socket ->
            socket.on("on.user.authorized") {
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()} Data $it")
                events.value = Event.Default(it)
                throw RuntimeException("on.user.authorized")
            }
            socket.on(Socket.EVENT_CONNECT) {
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()}")
                throw RuntimeException("connect")
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()}") // null
                throw RuntimeException("disconnect")
            }
            socket.on(Socket.EVENT_CONNECT_ERROR) {
                //options.auth.put("authorization", "bearer 1234")
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()} Error $it")
                socket.connect()
                throw RuntimeException("connect error $it")
            }
        }
    }

    fun auth(id: Int, token: String) {
        socket?.emit("user.auth", arrayOf(id, token))
    }
}