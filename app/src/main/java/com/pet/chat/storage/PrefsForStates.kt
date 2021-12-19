package com.pet.chat.storage

import android.content.Context
import android.content.SharedPreferences

class States(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("chat_states", Context.MODE_PRIVATE)

    var lastRooom: Int
        get() = preferences.getInt("lastRooom", -1)
        set(value) = preferences.edit().putInt("lastRooom", value).apply()

    var cameraFilePath: String
        get() = preferences.getString("cameraFilePath", "")!!
        set(value) = preferences.edit().putString("cameraFilePath", value).apply()
}