package com.pet.chat.base


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel

abstract class ComposeViewModel: ViewModel() {

    abstract fun onStart()

    fun onStop(){
        viewModelScope.cancel()
    }
}