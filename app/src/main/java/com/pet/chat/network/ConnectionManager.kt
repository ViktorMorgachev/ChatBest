package com.pet.chat.network

import android.util.Log
import com.google.gson.Gson
import com.pet.chat.helpers.toSocketData
import com.pet.chat.network.data.receive.*
import com.pet.chat.network.data.send.*
import com.pet.chat.providers.interfaces.EventFromServerProvider
import com.pet.chat.network.data.receive.ChatRead as ChatReadReceive
import com.pet.chat.network.data.receive.ChatDelete as ChatDeleteReceive
import com.pet.chat.network.data.receive.ChatHistory as ChatHistoryReceive
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

sealed class EventFromServer {
    data class AutorizationEvent(val data: UserAutorized) : EventFromServer()
    data class MessageNewEvent(val data: MessageNew) : EventFromServer()
    data class ChatHistoryEvent(val data: ChatHistoryReceive) : EventFromServer()
    data class ChatDeleteEvent(val data: ChatDeleteReceive) : EventFromServer()
    data class ChatClearEvent(val data: ChatClear) : EventFromServer()
    data class MessageDeleteEvent(val data: MessageDelete) : EventFromServer()
    data class ChatReadEvent(val data: ChatReadReceive) : EventFromServer()
    data class UserOnlineEvent(val data: UserOnline) : EventFromServer()

    object ConnectionSuccess : EventFromServer()
    data class ConnectionError(val data: String = "") : EventFromServer()
    object Disconnected : EventFromServer()
    object NO_INITIALIZED : EventFromServer()
}

sealed class EventToServer(val eventName: String, val any: Any) {
    data class AuthEvent(val data: UserAuth) : EventToServer("user.auth", any = data)
    data class CreateChatEvent(val data: ChatStart) : EventToServer("chat.start", any = data)
    data class SendMessageEvent(val data: SendMessage) : EventToServer("message.send", any = data)
    data class GetChatHistory(val data: ChatHistory) : EventToServer("chat.history", any = data)
    data class DeleteChat(val data: ChatDelete) : EventToServer("chat.delete", any = data)
    data class ClearChatEvent(val data: ClearChat) : EventToServer("chat.clear", any = data)
    data class DeleteMessageEvent(val data: DeleteMessage) : EventToServer("message.delete", any = data)
    data class ChatReadEvent(val data: ChatRead) : EventToServer("chat.read", any = data)
}

@Singleton
class ConnectionManager @Inject constructor(private val eventFromServerProvider: EventFromServerProvider) {

    private var socket: Socket? = null

    fun connectionActive(): Boolean {
        return socket?.isActive == true
    }

    private fun post(data: EventFromServer) {
        eventFromServerProvider.postEventFromServer(data)
    }

    fun initConnection(uri: String? = null, options: IO.Options? = null) {
        try {
            val optionsNew = IO.Options.builder()
                .setPath("/socket/")
                .setTransports(arrayOf("websocket", "polling"))
                .setPort(3001).build()

            val connectionUrlNew = "wss://vskvortsov.ru"
            socket = IO.socket(connectionUrlNew, optionsNew).connect()
            registratingEvents()
        } catch (error: Throwable) {
            error.printStackTrace()
        }
    }

    private fun registratingEvents() = runBlocking {
        socket?.let { socket ->
            socket.on("on.user.authorized") {
                val data = Gson().fromJson("${it[0]}", UserAutorized::class.java)
                post(EventFromServer.AutorizationEvent(data = data))
            }
            socket.on("connection") {
                Log.d(
                    "Socket",
                    "Socket: SocketID ${socket.id()} Connected ${socket.connected()} Data ${it.toSocketData()}"
                )
            }
            socket.on(Socket.EVENT_CONNECT) {
                post(EventFromServer.ConnectionSuccess)
                Log.d(
                    "Socket",
                    "Socket: Connect SocketID ${socket.id()} Connected ${socket.connected()}"
                )
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d(
                    "Socket",
                    "Socket: Disconnect SocketID ${socket.id()} Connected ${socket.connected()}"
                )
            }
            socket.on(Socket.EVENT_CONNECT_ERROR) {
                //options.auth.put("authorization", "bearer 1234")
                Log.d(
                    "Socket",
                    "Socket: SocketID ${socket.id()} Connected ${socket.connected()} Error $it"
                )
                post(EventFromServer.NO_INITIALIZED)
                post(EventFromServer.ConnectionError(data = "???????????? ????????????????????"))
                socket.close()
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
                post(EventFromServer.ChatDeleteEvent(data = data))
            }
            socket.on("on.chat.clear") {
                val data = Gson().fromJson("${it[0]}", ChatClear::class.java)
                post(EventFromServer.ChatClearEvent(data = data))
            }
            socket.on("on.message.delete") {
                val data = Gson().fromJson("${it[0]}", MessageDelete::class.java)
                post(EventFromServer.MessageDeleteEvent(data = data))
            }
            socket.on("on.chat.read") {
                val data = Gson().fromJson("${it[0]}", ChatReadReceive::class.java)
                post(EventFromServer.ChatReadEvent(data = data))
            }
            socket.on("on.user.online") {
                val data = Gson().fromJson("${it[0]}", UserOnline::class.java)
                post(EventFromServer.UserOnlineEvent(data = data))
            }
        }
    }

    fun postEventToServer(event: EventToServer, error: (String) -> Unit) {
        try {
            if (socket?.connected() == false)
                socket?.connect()
            socket?.emit(event.eventName, event.any.toSocketData())
        } catch (e: Throwable) {
            e.printStackTrace()
            error.invoke(": ?????? ???????????????? ????????????")
        }

    }

    fun tryToConnect() {
        try {
            socket?.connect()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}