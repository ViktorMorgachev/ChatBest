package com.pet.chat.ui.screens.chat

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pet.chat.App
import com.pet.chat.App.Companion.prefs
import com.pet.chat.helpers.observeAsState
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.ViewState
import com.pet.chat.network.data.base.Message
import com.pet.chat.network.data.base.File
import com.pet.chat.network.data.base.FilePreview
import com.pet.chat.network.data.send.*
import com.pet.chat.ui.*
import com.pet.chat.ui.theme.ChatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class BottomActionData(
    val image: ImageVector,
    val itemDescribe: String,
    val onClickAction: () -> Unit,
)

enum class State {
    Loading, Loaded, Error, None
}

fun Message.toSimpleMessage(): RoomMessage.SimpleMessage {
    val file = if (this.attachment == null) null else {
        File(
            this.attachment.room_id,
            type = this.attachment.type,
            null,
            this.attachment.file_id.toInt(), state = State.Loaded
        )
    }

    return RoomMessage.SimpleMessage(
        userID = user_id.toString(),
        date = created_at.toString(),
        text = text,
        isOwn = prefs?.userID == user_id.toInt(), messageID = this.id.toInt(),
        file = file
    )
}

val mockData: List<RoomMessage.SimpleMessage> = listOf(
    mockAliceMessage.copy(text = "Hi Bob"),
    mockAliceMessage.copy(text = "Hi Alice"),
    mockAliceMessage.copy(text = "How are you?"),
    mockBobMessage.copy(text = "I.m fine"),
    mockBobMessage,
    mockBobMessage,
    mockAliceMessage,
    mockBobMessage,
    mockAliceMessage
)

val mockDataBottomSheetItems = listOf(
    mockDataBottomSheetItem,
    BottomActionData(image = Icons.Outlined.FileUpload, itemDescribe = "FileSystem", {})
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Room(
    modifier: Modifier = Modifier,
    roomID: Int,
    actionProvider: MessagesViewModel.ActionProvider?,
    navController: NavController?,
    scope: CoroutineScope,
    cameraLauncher: () -> Unit,
    bottomSheetActions: List<BottomActionData> =
        listOf(
            BottomActionData(image = Icons.Outlined.Camera,
                itemDescribe = "Camera",
                onClickAction = { cameraLauncher.invoke() })
        ),
    viewModel: MessagesViewModel
) {
    val viewState = viewModel.viewStateProvider.viewState.observeAsState(ViewState.StateLoading)

    val messages = viewModel.chatProviderImpl.chats.collectAsState()


    DisposableEffect(key1 = viewModel) {
        viewModel.onStart()
        onDispose {
            viewModel.onStop()
        }
    }

    SideEffect {
        Log.d("RoomScreen", "ViewState ${viewState.value}")
    }

    LaunchedEffect(key1 = Unit, block = {
        viewModel.getChatHistory(
            EventToServer.GetChatHistory(
                ChatHistory(
                    lastId = 0,
                    limit = 20,
                    roomId = roomID
                )
            )
        )
    })

    if (App.states?.cameraFilePath!!.isNotEmpty()) {
        actionProvider?.resultAfterCamera(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Room $roomID")
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { actionProvider?.clearChatAction() }) {
                        Icon(Icons.Filled.ClearAll, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (true) {
            when (viewState.value) {
                is ViewState.StateLoading -> {
                    LoadingView()
                }
                is ViewState.Error -> {
                    ErrorView(
                        retryAction = { },
                        errorText = (viewState.value as ViewState.Error).errorInfo
                    )
                }
                is ViewState.StateNoItems -> {
                    NoItemsView(message = "Тут пока пусто, напишите что нибудь", iconResID = null)
                }
                is ViewState.Display -> {
                    val firstItem = (viewState.value as ViewState.Display).data.firstOrNull() as List<*>
                    if (firstItem.firstOrNull() is RoomMessage){
                        Log.d("RoomScreen", "Messages  ${firstItem.size}")
                        ChatTheme {
                            MessagesView(
                                modifier = Modifier
                                    .padding(innerPadding),
                                bottomSheetActions = bottomSheetActions,
                                scope = scope,
                                roomID = roomID,
                                actionProvider = actionProvider, messages = (firstItem as List<RoomMessage>).sortedBy { it.messageID }
                            )
                        }
                    } else {
                        Log.d("RoomScreen", "Cast Exception need RoomMessage was ${firstItem.first()}")
                    }

                }
            }
        }

    }

}


@Preview(widthDp = 400, showSystemUi = false)
@Composable
fun MessagesViewPreview() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Room $")
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Filled.ClearAll, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { innerPadding ->
        ChatTheme {
            MessagesView(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                bottomSheetActions = listOf(
                    BottomActionData(image = Icons.Outlined.Camera,
                        itemDescribe = "Camera",
                        onClickAction = {})
                ),
                scope = rememberCoroutineScope(),
                roomID = -1,
                actionProvider = null,
                messages = listOf()
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessagesView(
    modifier: Modifier = Modifier,
    bottomSheetActions: List<BottomActionData>,
    scope: CoroutineScope,
    roomID: Int,
    actionProvider: MessagesViewModel.ActionProvider?,
    messages: List<RoomMessage>
) {

    SideEffect {
        Log.d("Screen", "MessagesView")
    }

    val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val openDialogEvent = remember { mutableStateOf<FilePreview?>(null) }
    ModalBottomSheetLayout(
        sheetContent = {
            LazyRow() {
                items(bottomSheetActions) { item ->
                    BottomSheetItem(itemData = item, closeBottomAction = {
                        scope.launch {
                            modalBottomSheetState.hide()
                        }
                    })
                }
            }
        },
        sheetState = modalBottomSheetState
    )
    {
        val (message, messageChange) = rememberSaveable { mutableStateOf("") }
        val (openDialog, openDialogChange) = remember { mutableStateOf(false) }

        if (openDialogEvent.value != null) {
            openDialogChange(true)
        } else openDialogChange(false)

        if (openDialog) {
            val file = File(
                roomID = roomID,
                type = "photo",
                App.states?.cameraFilePath,
                state = State.None
            )
            FilePreviewDialog(
                file = file,
                applyMessage = { text ->
                    actionProvider?.applyMessageAction(
                        text = message,
                        file = file
                    )
                },
                closeDialog = { openDialogChange.invoke(true) })
        }

        val sendAction = {
            actionProvider?.sendMessageAction(
                SendMessage(
                    roomId = roomID,
                    text = message,
                    attachmentId = null
                )
            )
            messageChange("")
        }

        Column() {
            if (messages.isEmpty()){
                NoItemsView(message = "Напишите одно сообщение", iconResID = null, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    items(messages) { message ->
                        MessageItem(modifier = modifier.padding(all = 4.dp),
                            message = message,
                            deleteMessageAction = {
                                when (message) {
                                    is RoomMessage.SendingMessage -> {
                                        actionProvider?.deleteMessageAction(message)
                                    }
                                    is RoomMessage.SimpleMessage -> {
                                        actionProvider?.deleteMessageAction(message)
                                    }
                                }
                            },
                            tryUploadAction = { actionProvider?.tryUploadFileAction(data = message as RoomMessage.SendingMessage) },
                            tryDownloadAction = { actionProvider?.tryToDownLoadAction(data = message as RoomMessage.SimpleMessage) }
                        )
                    }
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                Row() {
                    TextField(
                        modifier = Modifier,
                        value = message,
                        onValueChange = messageChange
                    )
                    IconButton(onClick = { sendAction() }, enabled = message.isNotEmpty()) {
                        Icon(Icons.Filled.Send, contentDescription = "Send")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            if (!modalBottomSheetState.isVisible)
                                modalBottomSheetState.show()
                            else modalBottomSheetState.hide()
                        }
                    }) {
                        Icon(Icons.Filled.Attachment, contentDescription = "AttachFile")
                    }

                }

                Button(
                    onClick = { sendAction() },
                    enabled = message.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Отправить")
                }
            }

        }
    }
}

@Composable
fun openFilePreviewDialog(
    file: File,
) {
    ChatTheme {
        FilePreviewDialog(
            file = file,
            applyMessage = { },
            closeDialog = {})
    }
}

@Composable
fun BottomSheetItemPreview() {
    ChatTheme {
        LazyRow() {
            items(mockDataBottomSheetItems) { item ->
                BottomSheetItem(itemData = item, closeBottomAction = {})
            }
        }
    }
}






