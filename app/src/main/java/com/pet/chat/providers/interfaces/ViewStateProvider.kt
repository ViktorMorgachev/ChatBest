package com.pet.chat.providers.interfaces

import kotlinx.coroutines.flow.MutableStateFlow


sealed class ViewState{
    object StateLoading: ViewState()
    object StateNoItems: ViewState()
    data class Error(val errorInfo: String): ViewState()
    data class Display(val data: Any? = null): ViewState()
    object Success: ViewState()
}

interface ViewStateProvider {
    val viewState:  MutableStateFlow<ViewState>
    fun postViewState(viewState: ViewState)
}