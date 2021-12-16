package com.pet.chat.network.data.base

import com.pet.chat.network.data.Message

data class Messages(val list: List<Message>, val roomID: Int)