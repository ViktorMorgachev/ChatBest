package com.pet.lovefinder.network

import com.google.gson.Gson
import com.pet.lovefinder.helpers.toSocketData
import com.pet.lovefinder.network.data.receive.MessageNew
import com.pet.lovefinder.network.data.receive.UserAutorized
import com.pet.lovefinder.network.data.send.*
import com.pet.lovefinder.network.data.receive.ChatDelete as ChatDeleteReceive
import com.pet.lovefinder.network.data.receive.ChatHistory as ChatHistoryReceive
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.runBlocking

sealed class EventFromServer {
    data class Autorization(val data: UserAutorized) : EventFromServer()
    data class MessageNewEvent(val data: MessageNew) : EventFromServer()
    data class ChatHistoryEvent(val data: ChatHistoryReceive) : EventFromServer()
    data class ChatDelete(val data: ChatDeleteReceive) : EventFromServer()

    data class MessageSend(
        val room_id: Number,
        val text: String,
        val attachment_id: Int?,
    ) : EventFromServer()

    data class ConnectionSuccess(val info: String) : EventFromServer()
    data class ConnectionError(val info: String) : EventFromServer()
    data class Disconnected(val info: String) : EventFromServer()
    data class Default(val data: Any?) : EventFromServer()
    data class Debug(val data: Any?) : EventFromServer()

}

interface Subscriber {
    fun post(eventFromServer: EventFromServer)
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

    private fun post(data: EventFromServer) {
        subscribers.forEach {
            it.post(eventFromServer = data)
        }
    }

    fun initConnection(uri: String, options: IO.Options) {
        try {
            socket = IO.socket("ws://185.26.121.63:3000").connect()
            registratingEvents()
        } catch (error: Throwable) {
            error.printStackTrace()
        }
    }

    private fun registratingEvents() = runBlocking {
        socket?.let { socket ->
            socket.on("on.user.authorized") {
                val data = Gson().fromJson("${it[0]}", UserAutorized::class.java)
                post(EventFromServer.Autorization(data = data))
            }
            socket.on("connection") {
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()} Data $it")
            }
            socket.on(Socket.EVENT_CONNECT) {
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()}")
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()}") // null
            }
            socket.on(Socket.EVENT_CONNECT_ERROR) {
                //options.auth.put("authorization", "bearer 1234")
                println("Socket: SocketID ${socket.id()} Connected ${socket.connected()} Error $it")
                socket.connect()
            }
            socket.on("on.message.new") {
                val data = Gson().fromJson("${it[0]}", MessageNew::class.java)
                post(EventFromServer.MessageNewEvent(data = data))
            }
            socket.on("on.chat.history") {
                val data = Gson().fromJson("${it[0]}", ChatHistoryReceive::class.java)
                post(EventFromServer.ChatHistoryEvent(data = data))
            }
            socket.on("on.chat.delete") {
                val data = Gson().fromJson("${it[0]}", ChatDeleteReceive::class.java)
                post(EventFromServer.ChatDelete(data = data))
            }
        }
    }

    fun auth(userAuth: UserAuth) {
        socket?.emit("user.auth", userAuth.toSocketData())
    }

    fun createChat(chat: ChatStart) {
        socket?.emit("chat.start", chat.toSocketData())
    }

    fun sendMesages(sendMessage: SendMessage){
        socket?.emit("message.send", sendMessage.toSocketData())
    }

    fun getChatHistory(chatHistory: ChatHistory) {
        socket?.emit("chat.history", chatHistory.toSocketData())
    }

    fun deleteChat(chatDelete: ChatDelete) {
        socket?.emit("chat.delete", chatDelete.toSocketData())
    }
}