package com.pet.lovefinder.ui

import androidx.annotation.StringRes
import com.pet.lovefinder.R

sealed class Screen(val route: String, @StringRes val resourceId: Int){
    object Autorization : Screen("Autorization", R.string.autorization)
    object Chats: Screen("Chats", R.string.chats){

    }
    object CreateChat: Screen("CreateChat", R.string.createChat)
    object Room: Screen("{roomID}/Room", R.string.room){
        fun createRoute(roomID: String) = "$roomID/Room"
    }
}
