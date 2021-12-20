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
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.events.InternalEvent
import com.pet.chat.helpers.fileUploadWorkerTag
import com.pet.chat.helpers.isWorkScheduled
import com.pet.chat.helpers.workDataOf
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.receive.ChatDelete
import com.pet.chat.network.data.send.*
import com.pet.chat.network.data.toChatItemInfo
import com.pet.chat.network.workers.*
import com.pet.chat.ui.*
import com.pet.chat.ui.theme.ChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val eventViewModel by viewModels<ChatViewModel>()
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
            ChatTheme {
                MyApp(eventViewModel)
            }
        }


        resultAfterCameraPermission = { granted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    granted -> {
                        eventViewModel.takePicture(this,
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
                eventViewModel.takePicture(this, launchCamera = { cameraLauncher.launch(it) })
            }
        }

        resultAfterCamera = {
            Log.d("MainActivity",
                "result after camera and last file ${eventViewModel.imageUri?.path}")
            if (it) {
                eventViewModel.postInternalAction(internalEvent = InternalEvent.OpenFilePreview(
                    fileUri = eventViewModel.imageUri,
                    filePath = null,
                    openDialog = true))
            }
        }

    }

    fun startLoadPhoto(messageWithFile: MessageWithFile) {
        val workBuilder =
            OneTimeWorkRequestBuilder<FileUploadWorker>().addTag(fileUploadWorkerTag).apply {
                setInputData(workDataOf(roomID to messageWithFile.file.roomID,
                    type to messageWithFile.file.type,
                    filePath to messageWithFile.file.filePath,
                    messageID to messageWithFile.messageID,
                    text to messageWithFile.text
                ))
            }.build()
        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(workBuilder)
        if (!workManager.isWorkScheduled(fileUploadWorkerTag)) {
            workManager.enqueue(workBuilder)
        }
    }

    fun startDownloadPhoto() {
    }

    @Composable
    fun MyApp(viewModel: ChatViewModel) {
        val navController = rememberNavController()
        val event = viewModel.events.collectAsState()
        val internalEvents = viewModel.internalEvents.collectAsState()

        Log.d("DebugInfo: ",
            "User autentificated: ${App.prefs?.identified()} Current Room ${App.states?.lastRooom}")

        observeEvent(event, viewModel)
        observe(internalEvents, viewModel)

        NavHost(navController = navController, startDestination = Screen.Autorization.route) {
            composable(Screen.Autorization.route) {
                AutorizationScreen(onAuthEvent = {
                    viewModel.postEventToServer(EventToServer.AuthEvent(it))
                },
                    navController = navController).also {
                    App.states?.lastRooom = -1
                }
            }
            composable(Screen.Chats.route) {
                ChatsScreen(
                    navController = navController,
                    deleteChat = { viewModel.postEventToServer(EventToServer.DeleteChat(it)) },
                    openChat = {
                        viewModel.postEventToServer(EventToServer.GetChatHistory(it))
                        navController.navigate(Screen.Room.createRoute(it.roomId.toString()))
                    }, viewModel = viewModel).also {
                    App.states?.lastRooom = -1
                }
            }
            composable(Screen.CreateChat.route) {
                CreateChatScreen(value = event.value,
                    createChat = { viewModel.postEventToServer(EventToServer.CreateChatEvent(it)) },
                    navController = navController).also {
                    App.states?.lastRooom = -1
                }
            }
            composable(Screen.Room.route) { backStackEntry ->
                val roomID = backStackEntry.arguments?.getString("roomID")
                requireNotNull(roomID) { "roomID parameter wasn't found. Please make sure it's set!" }

                Chat(sendMessage = { viewModel.postEventToServer(EventToServer.SendMessageEvent(it)) },
                    roomID = roomID.toInt(),
                    navController = navController,
                    clearChat = {
                        viewModel.postEventToServer(EventToServer.ClearChatEvent(ClearChat(roomID.toInt())))
                    },
                    deleteMessageAction = {
                        if (it is RoomMessage.SendingMessage) {
                            viewModel.deleteMessage(it)
                        } else {
                            viewModel.postEventToServer(EventToServer.DeleteMessageEvent(
                                DeleteMessage(it.messageID)))
                        }
                    },
                    eventChatRead = { viewModel.postEventToServer(EventToServer.ChatReadEvent(it)) },
                    scope = rememberCoroutineScope(),
                    cameraLauncher = { cameraPermissionContract.launch(Manifest.permission.CAMERA) },
                    viewModel = viewModel,
                    internalEvent = internalEvents.value,
                    tryLoadFileAction = {
                        startLoadPhoto(MessageWithFile(
                            File(roomID = roomID.toInt(),
                                type = it.fileType,
                                filePath = it.filePath), messageID = it.messageID, text = it.text
                        ))
                    },
                    tryToDownLoadAction = {
                        startDownloadPhoto()
                    },
                    applyMessageAction = {
                        viewModel.addMessage(
                            roomMessage = RoomMessage.SendingMessage(
                                text = it.text,
                                isOwn = true,
                                filePath = it.file.filePath,
                                fileState = FileState.Loading,
                                messageID = it.messageID,
                                userID = App.prefs?.userID.toString(),
                                fileType = it.file.type,
                                date = null
                            ))
                        startLoadPhoto(it)
                    }
                ).also {
                    App.states?.lastRooom = roomID.toInt()
                }
            }
        }
        checkForRestoringState(navController, viewModel)
    }

    private fun checkForRestoringState(navController: NavHostController, viewModel: ChatViewModel) {
        if (App.prefs?.identified() == true) {
            navController.navigate(Screen.Chats.route)
        }
        if (App.states?.lastRooom != -1) {
            navController.navigate(Screen.Room.createRoute(App.states?.lastRooom.toString()))
            if (App.states?.cameraFilePath!!.isNotEmpty()) {
                viewModel.postInternalAction(internalEvent = InternalEvent.OpenFilePreview(fileUri = null,
                    filePath = App.states?.cameraFilePath!!))
            }
        }
    }

    @Composable
    fun observeEvent(event: State<EventFromServer>, viewModel: ChatViewModel) {
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
                        eventViewModel.postEventToServer(EventToServer.AuthEvent(UserAuth(id = App.prefs?.userID!!,
                            token = App.prefs?.userToken!!)))
                        if (App.states?.lastRooom != -1) {
                            eventViewModel.postEventToServer(EventToServer.GetChatHistory(
                                ChatHistory(limit = 10,
                                    roomId = eventViewModel.currentRoom,
                                    lastId = null)))
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
                    viewModel.deleteMessage(data)
                }
                is EventFromServer.ChatReadEvent -> {
                    val data = (event.value as EventFromServer.ChatReadEvent).data
                    viewModel.updateChatState(data)
                }
                is EventFromServer.UserOnlineEvent -> {
                    val data = (event.value as EventFromServer.UserOnlineEvent).data
                    viewModel.updateUserStatus(data)
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

    @Composable
    fun observe(event: State<InternalEvent>, viewModel: ChatViewModel) {
        try {
            Log.d("InternalEvent", "${event.value}")
            when (event.value) {
                is InternalEvent.FileErrorUpload -> {
                    val data = (event.value as InternalEvent.FileErrorUpload).messageID
                    viewModel.fileUploadError(messageID = data)
                }
                else -> {}
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.makeText(this,
                stringResource(id = R.string.something_went_wrong) + ": при получении обработке внутрених событий",
                Toast.LENGTH_LONG).show()
        }
    }
}







