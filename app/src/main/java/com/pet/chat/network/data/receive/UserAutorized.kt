package com.pet.chat.network.data.receive

import com.pet.chat.network.data.base.Dialog

data class UserAutorized(
    var dialogs: List<Dialog>,
    var success: Boolean?,
    var token: String?,
    var user: User
) {
    data class User(
        var email: String?,
        var id: Int
    )
}