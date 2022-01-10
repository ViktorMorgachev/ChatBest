package com.pet.chat.network.data

sealed class ViewState{
    object StateLoading: ViewState()
    object StateNoItems: ViewState()
    data class Error(val errorInfo: String): ViewState()
    data class Display(val data: List<Any> = listOf()): ViewState()
    class Success: ViewState()
}
