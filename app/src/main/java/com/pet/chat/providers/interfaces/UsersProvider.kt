package com.pet.chat.providers.interfaces

import com.pet.chat.network.data.base.User
import kotlinx.coroutines.flow.MutableStateFlow

interface UsersProvider{
    val users :MutableStateFlow<List<User>>
    fun updateUserStatus(data: Any)
    fun fetchAllUsers(): List<User>
}