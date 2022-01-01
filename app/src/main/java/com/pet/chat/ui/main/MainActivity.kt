package com.pet.chat.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.receive.ChatDelete
import com.pet.chat.network.data.send.*
import com.pet.chat.network.data.toChatItemInfo
import com.pet.chat.ui.*
import com.pet.chat.ui.chatflow.chatFlow
import com.pet.chat.ui.theme.ChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var resultAfterCamera: ((Boolean) -> Unit)? = null
    var resultAfterCameraPermission: ((Boolean) -> Unit)? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        resultAfterCamera?.invoke(it)
    }
    private val cameraPermissionContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            resultAfterCameraPermission!!.invoke(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Screen.Autorization.route) {
                chatFlow(navController)
            }

            ChatTheme {
                MyApp(navController)
            }
        }


    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MyApp(navController: NavHostController) {
        val viewModel = hiltViewModel<ChatViewModel>()
        val event = viewModel.events.collectAsState()

        resultAfterCameraPermission = { granted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    granted -> {
                        viewModel.takePicture(this,
                            launchCamera = { cameraLauncher.launch(it) })
                    }
                    !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                        // доступ к камере запрещен, пользователь поставил галочку Don't ask again.
                    }
                    else -> {
                        // доступ к камере запрещен, пользователь отклонил запрос
                    }
                }
            } else {
                viewModel.takePicture(this, launchCamera = { cameraLauncher.launch(it) })
            }
        }

        viewModel.cameraPermissionContract = this.cameraPermissionContract

        resultAfterCamera = {
            viewModel.resultAfterCamera(it)
        }

        Log.d(
            "DebugInfo: ",
            "User autentificated: ${App.prefs?.identified()} Current Room ${App.states?.lastRooom}"
        )

        observeNetworkEvent(event)
        checkForRestoringState(navController, viewModel)
    }

    private fun checkForRestoringState(navController: NavHostController, viewModel: ChatViewModel) {

        if (App.prefs?.identified() == true) {
            navController.navigate(Screen.Chats.route)
        }
        if (App.states?.lastRooom != -1) {
            navController.navigate(Screen.Room.createRoute(App.states?.lastRooom.toString()))
            if (App.states?.cameraFilePath!!.isNotEmpty()) {
                viewModel.resultAfterCamera(true)
            }
        }
    }

    @Composable
    fun observeNetworkEvent(event: State<EventFromServer>) {
        val viewModel = hiltViewModel<ChatViewModel>()
        try {
            Log.d("EventFromServer", "${event.value}")
            when (event.value) {
                is EventFromServer.AutorizationEvent -> {
                    if ((event.value as EventFromServer.AutorizationEvent).data.success == true) {
                        val data = (event.value as EventFromServer.AutorizationEvent).data
                        App.prefs?.saveUser(UserAuth(data.user.id, token = data.token!!))
                        viewModel.updateChat(data.dialogs.map { it.toChatItemInfo() })
                    }
                }
                is EventFromServer.ConnectionSuccess -> {
                    if (App.prefs?.identified() == true) {
                        viewModel.postEventToServer(
                            EventToServer.AuthEvent(
                                UserAuth(
                                    id = App.prefs?.userID!!,
                                    token = App.prefs?.userToken!!
                                )
                            )
                        )
                        if (App.states?.lastRooom != -1) {
                            viewModel.postEventToServer(
                                EventToServer.GetChatHistory(
                                    ChatHistory(
                                        limit = 10,
                                        roomId = App.states?.lastRooom!!,
                                        lastId = null
                                    )
                                )
                            )
                        }
                    }
                }
                is EventFromServer.MessageNewEvent -> {
                    val data = (event.value as EventFromServer.MessageNewEvent).data
                    viewModel.updateChat(data.toChatItemInfo())
                }
                is EventFromServer.ChatHistoryEvent -> {
                    val data = (event.value as EventFromServer.ChatHistoryEvent).data
                    data.toChatItemInfo()?.let { chatItemInfo ->
                        viewModel.updateChat(chatItemInfo)
                    }

                }
                is EventFromServer.ChatDeleteEvent -> {
                    val data = (event.value as EventFromServer.ChatDeleteEvent).data
                    viewModel.deleteChat(ChatDelete(chat = data.chat, room = data.room))
                }
                is EventFromServer.ChatClearEvent -> {
                    val data = (event.value as EventFromServer.ChatClearEvent).data
                    viewModel.clearChat(data)
                }
                is EventFromServer.MessageDeleteEvent -> {
                    val data = (event.value as EventFromServer.MessageDeleteEvent).data
                    viewModel.deleteMessage(data, data.room.id.toInt())
                }
                is EventFromServer.ChatReadEvent -> {
                    val data = (event.value as EventFromServer.ChatReadEvent).data
                    viewModel.updateChatState(data)
                }
                is EventFromServer.UserOnlineEvent -> {
                    val data = (event.value as EventFromServer.UserOnlineEvent).data
                    viewModel.updateUserStatus(data)
                }
                else -> {
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.makeText(
                this,
                stringResource(id = R.string.something_went_wrong) + ": при получении данных",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}







