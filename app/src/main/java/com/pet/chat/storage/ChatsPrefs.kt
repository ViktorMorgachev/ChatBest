package com.pet.chat.storage

import android.content.Context
import android.content.SharedPreferences
import com.pet.chat.network.data.send.UserAuth

class ChatsPrefs(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)

    fun saveUser(userAuth: UserAuth) {
        userID = userAuth.id.toInt()
        userToken = userAuth.token
    }

    fun identified(): Boolean {
        return userID != -1 && !userToken.isBlank()
    }

    var lastRoom: Int
        get() = preferences.getInt("lastRoomID", -1)
        set(value) = preferences.edit().putInt("lastRoomID", value).apply()

    var cameraFilePath: String
        get() = preferences.getString("cameraFilePath", "")!!
        set(value) = preferences.edit().putString("cameraFilePath", value).apply()

    var userID: Int
        get() = preferences.getInt("userID", -1)
        set(value) = preferences.edit().putInt("userID", value).apply()

    var userToken: String
        get() = preferences.getString("userToken", "")!!
        set(value) = preferences.edit().putString("userToken", value).apply()
}