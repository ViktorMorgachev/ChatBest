package com.pet.chat.providers

import com.pet.chat.network.data.ViewState
import com.pet.chat.providers.interfaces.ViewStateProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ViewStateProviderImpl @Inject constructor(override val viewState: MutableStateFlow<ViewState>) :
    ViewStateProvider {
    override fun postViewState(viewState: ViewState) {
        runBlocking(Dispatchers.Main) {
            this@ViewStateProviderImpl.viewState.emit(viewState)
        }
    }
}