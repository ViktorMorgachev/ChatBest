package com.pet.chat.providers

import com.pet.chat.network.data.base.User
import com.pet.chat.network.data.receive.UserOnline
import com.pet.chat.providers.interfaces.UsersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersProviderImpl  @Inject constructor(override val users: MutableStateFlow<List<User>>): UsersProvider {

    override fun updateUserStatus(data: Any) {
        if (data is UserOnline){
        }
    }

    override fun fetchAllUsers(): List<User> {
        TODO("Not yet implemented")
    }
}