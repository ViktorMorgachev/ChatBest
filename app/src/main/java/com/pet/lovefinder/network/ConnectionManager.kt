package com.pet.lovefinder.network

import com.pet.lovefinder.network.data.AuthData
import com.pet.lovefinder.network.data.Dialog
import com.pet.lovefinder.network.data.User
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

sealed class Event {
    data class DeleteMessage(val messageID: String) : Event()
    data class Autorization(
        val success: Boolean,
        val dialogs: List<Dialog>,
        val user: User? = null,
    ) : Event()

    data class MessageSend(
        val room_id: Number,
        val text: String,
        val attachment_id: Int?,
    ) : Event()

    data class ConnectionSuccess(val info: String) : Event()
    data class ConnectionError(val info: String) : Event()
    data class Disconnected(val info: String) : Event()
    data class Default(val data: Any?) : Event()

}

interface Subscriber {
    fun post(event: Event)
}

object ConnectionManager {

    private var socket: Socket? = null
    private val subscribers: ArrayList<Subscriber> = arrayListOf()

    fun connectionActive(): Boolean {
        return socket?.isActive == true
    }

    fun subsribe(subscriber: Subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber)
        }
    }

    private fun post(data: Event) {
        subscribers.forEach {
            it.post(event = data)
        }
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

    fun auth(authData: AuthData) {
        socket?.emit("user.auth", authData)
    }
}