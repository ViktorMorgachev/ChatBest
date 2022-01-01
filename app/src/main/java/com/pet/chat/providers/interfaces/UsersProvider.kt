package com.pet.chat.providers.interfaces

interface UsersProvider<T> {
    fun updateUserStatus(data: Any)
    fun fetchAllUsers(): List<T>
}