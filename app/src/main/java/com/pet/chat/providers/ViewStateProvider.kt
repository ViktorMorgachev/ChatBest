package com.pet.chat.providers

import com.pet.chat.providers.interfaces.ViewStateProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewStateProviderProvider @Inject constructor() {
    val currentViewStateProvider: ViewStateProvider? = null
}