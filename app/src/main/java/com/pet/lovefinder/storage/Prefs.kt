package com.pet.lovefinder.storage

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("lovefinder", Context.MODE_PRIVATE)

    var userID: Int
        get() = preferences.getInt("userID", -1)
        set(value) = preferences.edit().putInt("userID", value).apply()

    var userToken: String?
        get() = preferences.getString("userToken", "")
        set(value) = preferences.edit().putString("userToken", value).apply()
}