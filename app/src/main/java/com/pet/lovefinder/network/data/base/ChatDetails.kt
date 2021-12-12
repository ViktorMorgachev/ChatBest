package com.pet.lovefinder.network.data.base

import com.pet.lovefinder.network.data.Chat
import com.pet.lovefinder.network.data.User

data class ChatDetails(val chat: Chat, val roomID: Int, val users: ArrayList<User>)
