package com.pet.chat.storage

import android.content.Context
import android.content.SharedPreferences
import com.pet.chat.network.data.send.UserAuth

class Prefs(context: Context) {

    fun saveUser(userAuth: UserAuth) {
        userID = userAuth.id.toInt()
        userToken = userAuth.token
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences("chat", Context.MODE_PRIVATE)

    var userID: Int
        get() = preferences.getInt("userID", -1)
        set(value) = preferences.edit().putInt("userID", value).apply()

    var userToken: String?
        get() = preferences.getString("userToken", "")
        set(value) = preferences.edit().putString("userToken", value).apply()
}