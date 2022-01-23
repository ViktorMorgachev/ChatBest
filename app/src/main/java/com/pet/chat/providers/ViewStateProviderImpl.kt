package com.pet.chat.providers

import android.util.Log
import com.pet.chat.helpers.SingleLiveEvent
import com.pet.chat.network.data.ViewState
import com.pet.chat.providers.interfaces.ViewStateProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class ViewStateProviderImpl @Inject constructor(override val viewState: SingleLiveEvent<ViewState>) :
    ViewStateProvider {
    override fun postViewState(viewState: ViewState) {
        runBlocking(Dispatchers.Main) {
            Log.d("ViewStateProviderImpl", "Instance ${this@ViewStateProviderImpl} ViewState set $viewState")
            this@ViewStateProviderImpl.viewState.postValue(viewState)
            Log.d("ViewStateProviderImpl", "Instance ${this@ViewStateProviderImpl} ViewState get $viewState")
        }
    }
}