package com.pet.chat.ui.main

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.chat.App
import com.pet.chat.BuildConfig
import com.pet.chat.R
import com.pet.chat.providers.InternalEvent
import com.pet.chat.providers.InternalEventsProvider
import com.pet.chat.helpers.*
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.base.FilePreview
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.providers.interfaces.ViewState
import com.pet.chat.providers.ViewStateProviderImpl
import com.pet.chat.providers.interfaces.EventFromServerProviderImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val internalEventsProvider: InternalEventsProvider,
    val chatProviderImpl: MultipleChatProviderImpl,
    val viewStateProvider: ViewStateProviderImpl,
    val connectionManager: ConnectionManager,
    val eventFromServerProvider: EventFromServerProviderImpl
) : ViewModel() {

    lateinit var cameraPermissionContract: ActivityResultLauncher<String>
    var imageUri: Uri? = null

    fun postEventToServer(eventToServer: EventToServer) {
        Log.d("EventToServer", "$eventToServer")
        viewModelScope.launch(Dispatchers.IO) {
            connectionManager.postEventToServer(event = eventToServer, error = {
                viewModelScope.launch(Dispatchers.Main) {
                    val resultText =
                        App.instance.applicationContext.getText(R.string.something_went_wrong)
                            .toString() + it
                    Toast.makeText(App.instance.applicationContext, resultText, Toast.LENGTH_LONG)
                        .show()
                }
            })
            viewStateProvider.postViewState(ViewState.StateLoading)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            eventFromServerProvider.events.collect {
                viewModelScope.launch(Dispatchers.Main) {

                }
            }
        }

    }

    fun takePicture(context: Context, launchCamera: (Uri) -> Unit) = viewModelScope.launch {
        CoroutineScope(Dispatchers.IO).launch {
            ImageUtils.createImageFile(context)?.also { file ->
                imageUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".fileprovider", file
                )
            }
        }.join()
        imageUri?.let { launchCamera(it) }
    }

    fun resultAfterCamera(it: Boolean) = viewModelScope.launch(Dispatchers.Default) {

        Log.d(
            "MainActivity",
            "result after camera and last file ${imageUri?.path}"
        )
        if (it) {
            internalEventsProvider.internalEvents.emit(
                InternalEvent.OpenFilePreview(
                    FilePreview(
                        fileUri = imageUri,
                        filePath = null,
                        openDialog = true
                    )
                )
            )
        }
    }

    fun launchCamera() = viewModelScope.launch(Dispatchers.Main) {
        cameraPermissionContract.launch(Manifest.permission.CAMERA)
    }
}

