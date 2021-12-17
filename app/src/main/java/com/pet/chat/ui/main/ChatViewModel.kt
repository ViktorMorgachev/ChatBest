package com.pet.chat.ui.main

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.App
import com.pet.chat.BuildConfig
import com.pet.chat.R
import com.pet.chat.helpers.ImageUtils
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.Subscriber
import com.pet.chat.network.data.send.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    val events = MutableStateFlow<EventFromServer>(EventFromServer.NO_INITIALIZED)
    private var imageUri: Uri? = null

    fun postEventToServer(eventToServer: EventToServer) {
        ConnectionManager.postEventToServer(event = eventToServer, error = {
            val resultText = App.instance.applicationContext.getText(R.string.something_went_wrong)
                .toString() + it
            Toast.makeText(App.instance.applicationContext, resultText, Toast.LENGTH_LONG).show()
        })
    }

    init {
        viewModelScope.launch {
            ConnectionManager.subsribe(object : Subscriber {
                override fun post(eventFromServer: EventFromServer) {
                    viewModelScope.launch {
                        events.emit(eventFromServer)
                    }
                }
            })
        }

    }

    fun takePicture(context: Context, launchCamera: (Uri) -> Unit) =
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                ImageUtils.createImageFile(context)?.also { file ->
                    imageUri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file)
                }
            }.join()
            imageUri?.let { launchCamera(it) }
        }


}