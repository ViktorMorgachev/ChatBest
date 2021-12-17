package com.pet.chat.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import com.pet.chat.R
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.receive.ChatDelete
import com.pet.chat.network.data.send.ClearChat
import com.pet.chat.network.data.toChatItemInfo
import com.pet.chat.ui.ViewDataStorage
import com.pet.chat.ui.*
import com.pet.chat.ui.theme.ChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val eventViewModel by viewModels<ChatViewModel>()
    var resultAfterCamera: ((Boolean) -> Unit)? = null
    var resultAfterCameraPermission: ((Boolean) -> Unit)? = null
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
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
            ChatTheme {
                MyApp(eventViewModel)
            }
        }

    }

    @Composable
    fun MyApp(viewModel: ChatViewModel) {
        val navController = rememberNavController()
        val event = viewModel.events.collectAsState()
        val chats = ViewDataStorage.chats.collectAsState()


        resultAfterCameraPermission = { granted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    granted -> {
                        viewModel.takePicture(this, launchCamera = { cameraLauncher.launch(it) })
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

        resultAfterCamera = {
            print("Result is $it")
        }


        observe(event)

        NavHost(navController = navController, startDestination = Screen.Autorization.route) {
            composable(Screen.Autorization.route) {
                AutorizationScreen(onAuthEvent = {
                    viewModel.postEventToServer(EventToServer.AuthEvent(it))
                },
                    navController = navController)
            }
            composable(Screen.Chats.route) {
                ChatsScreen(
                    navController = navController,
                    deleteChat = { viewModel.postEventToServer(EventToServer.DeleteChat(it)) },
                    openChat = {
                        viewModel.postEventToServer(EventToServer.GetChatHistory(it))
                        navController.navigate(Screen.Room.createRoute(it.roomId.toString()))
                    }, chats = chats.value)
            }
            composable(Screen.CreateChat.route) {
                CreateChatScreen(value = event.value,
                    createChat = { viewModel.postEventToServer(EventToServer.CreateChatEvent(it)) },
                    navController = navController)
            }
            composable(Screen.Room.route) { backStackEntry ->
                val roomID = backStackEntry.arguments?.getString("roomID")
                requireNotNull(roomID) { "roomID parameter wasn't found. Please make sure it's set!" }
                chats.value.firstOrNull { it.roomID == roomID.toInt() }?.let { chatItemInfo ->
                    val messages = chatItemInfo.roomMessages
                    Chat(sendMessage = {
                        viewModel.postEventToServer(EventToServer.SendMessageEvent(it))
                    },
                        messages = messages.toList(),
                        roomID = roomID.toInt(),
                        navController = navController,
                        clearChat = {
                            viewModel.postEventToServer(EventToServer.ClearChatEvent(ClearChat(
                                roomID.toInt())))
                        },
                        deleteMessage = {
                            viewModel.postEventToServer(EventToServer.DeleteMessageEvent(it))
                        },
                        eventChatRead = { viewModel.postEventToServer(EventToServer.ChatReadEvent(it)) },
                        loadFileAction = {},
                        scope = rememberCoroutineScope(),
                        cameraLauncher = { cameraPermissionContract.launch(Manifest.permission.CAMERA) },
                        viewModel = viewModel
                    )
                }

            }
        }

    }

    @Composable
    fun observe(event: State<EventFromServer>) {
        try {
            print("EventFromServer: ${event.value}")
            when (event.value) {
                is EventFromServer.AutorizationEvent -> {
                    if ((event.value as EventFromServer.AutorizationEvent).data.success == true) {
                        val data = (event.value as EventFromServer.AutorizationEvent).data
                        ViewDataStorage.updateChat(data.dialogs.map { it.toChatItemInfo() })
                    }
                }
                is EventFromServer.MessageNewEvent -> {
                    val data = (event.value as EventFromServer.MessageNewEvent).data
                    ViewDataStorage.updateChat(data.toChatItemInfo())
                }
                is EventFromServer.ChatHistoryEvent -> {
                    val data = (event.value as EventFromServer.ChatHistoryEvent).data
                    data.toChatItemInfo()?.let { chatItemInfo ->
                        ViewDataStorage.updateChat(chatItemInfo)
                    }

                }
                is EventFromServer.ChatDeleteEvent -> {
                    val data = (event.value as EventFromServer.ChatDeleteEvent).data
                    ViewDataStorage.deleteChat(ChatDelete(chat = data.chat, room = data.room))
                }
                is EventFromServer.ChatClearEvent -> {
                    val data = (event.value as EventFromServer.ChatClearEvent).data
                    ViewDataStorage.clearChat(data)
                }
                is EventFromServer.MessageDeleteEvent -> {
                    val data = (event.value as EventFromServer.MessageDeleteEvent).data
                    ViewDataStorage.deleteMessage(data)
                }
                is EventFromServer.ChatReadEvent -> {
                    val data = (event.value as EventFromServer.ChatReadEvent).data
                    ViewDataStorage.updateChatState(data)
                }
                is EventFromServer.UserOnlineEvent -> {
                    val data = (event.value as EventFromServer.UserOnlineEvent).data
                    ViewDataStorage.updateUserStatus(data)
                }
                else -> {}
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.makeText(this,
                stringResource(id = R.string.something_went_wrong) + ": при получении данных",
                Toast.LENGTH_LONG).show()
        }
    }
}







