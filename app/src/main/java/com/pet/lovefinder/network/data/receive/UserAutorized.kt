package com.pet.lovefinder.network.data.receive

import com.pet.lovefinder.network.data.Dialog

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