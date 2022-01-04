package com.pet.chat.providers.interfaces

import com.pet.chat.network.data.ViewState
import kotlinx.coroutines.flow.MutableStateFlow

interface ViewStateProvider {
    val viewState:  MutableStateFlow<ViewState>
    fun postViewState(viewState: ViewState)
}