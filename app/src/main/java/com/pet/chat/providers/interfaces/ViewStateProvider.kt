package com.pet.chat.providers.interfaces

import com.pet.chat.helpers.SingleLiveEvent
import com.pet.chat.network.data.ViewState

interface ViewStateProvider {
    val viewState:  SingleLiveEvent<ViewState>
    fun postViewState(viewState: ViewState)
}