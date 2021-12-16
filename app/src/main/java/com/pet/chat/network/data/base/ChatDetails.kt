package com.pet.chat.network.data.base

import com.pet.chat.network.data.Chat
import com.pet.chat.network.data.User

data class ChatDetails(val chat: Chat, val roomID: Int, val users: ArrayList<User>)
