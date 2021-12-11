package com.pet.lovefinder.network

import io.socket.client.IO
import io.socket.client.Socket

object ConnectionManager {

    private var socket: Socket? = null

    fun connectionActive(): Boolean {
        return socket?.isActive == true
    }

    fun initConnection(uri: String, options: IO.Options) {
        try {
            socket = IO.socket(uri, options)
            registratingEvents()
            auth()
        } catch (error: Throwable) {
            error.printStackTrace()
        }
    }

    private fun registratingEvents() {
        socket?.let { socket ->
            socket.on("connection") { args -> println(args) }
            socket.on("on.user.authorized") { print(it) }
        }
    }

    fun auth() {
        socket?.emit("user.auth", arrayOf("84", "newtoken123"))
    }
}