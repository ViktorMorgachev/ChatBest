package com.pet.chat.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pet.chat.App
import com.pet.chat.App.Companion.prefs
import com.pet.chat.events.InternalEvent
import com.pet.chat.network.data.base.Message
import com.pet.chat.network.data.send.ChatRead
import com.pet.chat.network.data.send.File
import com.pet.chat.network.data.send.MessageWithFile
import com.pet.chat.network.data.send.SendMessage
import com.pet.chat.network.workers.messageID
import com.pet.chat.ui.main.ChatViewModel
import com.pet.chat.ui.theme.ChatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class BottomActionData(
    val image: ImageVector,
    val itemDescribe: String,
    val onClickAction: () -> Unit,
)

sealed class RoomMessage(
    open val isOwn: Boolean,
    open val messageID: Int,
    open val userID: String,
    open val text: String,
    open val date: String?,
) {
    data class SendingMessage(
        val filePath: String,
        var fileState: FileState,
        val fileType: String,
        override val isOwn: Boolean = false,
        override val messageID: Int,
        override val userID: String,
        override val text: String,
        override val date: String?,
    ) : RoomMessage(isOwn, messageID, userID, text, date)

    data class SimpleMessage(
        val roomAttachment: RoomAttachment? = null,
        override val isOwn: Boolean,
        override val messageID: Int,
        override val userID: String,
        override val text: String,
        override val date: String?,
    ) : RoomMessage(isOwn, messageID, userID, text, date)
}

enum class FileState {
    Loading, Loaded, Error
}

data class RoomAttachment(
    val id: Int,
    val type: String,
    val fileID: Int,
    val filePath: String?,
    val fileState: FileState = if (filePath == null) FileState.Loading else FileState.Loaded,
)

fun Message.toSimpleMessage(): RoomMessage.SimpleMessage {
    return RoomMessage.SimpleMessage(userID = user_id.toString(),
        date = created_at.toString(),
        text = text,
        isOwn = prefs?.userID == user_id.toInt(), messageID = this.id.toInt())
}

val mockData: List<RoomMessage.SimpleMessage> = listOf(mockAliceMessage.copy(text = "Hi Bob"),
    mockAliceMessage.copy(text = "Hi Alice"),
    mockAliceMessage.copy(text = "How are you?"),
    mockBobMessage.copy(text = "I.m fine"),
    mockBobMessage,
    mockBobMessage,
    mockAliceMessage,
    mockBobMessage,
    mockAliceMessage)

val mockDataBottomSheetItems = listOf(mockDataBottomSheetItem,
    BottomActionData(image = Icons.Outlined.FileUpload, itemDescribe = "FileSystem", {}))

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun ChatListPrewiew() {
    ChatTheme {
        val dataForTesting = listOf(
            BottomActionData(image = Icons.Outlined.Camera,
                itemDescribe = "Camera",
                onClickAction = {}))
        Chat(
            sendMessage = {},
            roomID = -1,
            clearChat = {},
            navController = null,
            deleteMessageAction = { },
            eventChatRead = {},
            scope = rememberCoroutineScope(),
            cameraLauncher = { },
            viewModel = viewModel(),
            internalEvent = InternalEvent.None,
            tryLoadFileAction = {},
            tryToDownLoadAction = {},
            applyMessageAction = {}
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
// TODO слишком много параметров, нужно будет рефакторить
fun Chat(
    modifier: Modifier = Modifier,
    sendMessage: (SendMessage) -> Unit,
    roomID: Int,
    clearChat: () -> Unit,
    navController: NavController?,
    deleteMessageAction: (RoomMessage) -> Unit,
    eventChatRead: (ChatRead) -> Unit,
    scope: CoroutineScope,
    cameraLauncher: () -> Unit,
    bottomSheetActions: List<BottomActionData> =
        listOf(BottomActionData(image = Icons.Outlined.Camera,
            itemDescribe = "Camera",
            onClickAction = { cameraLauncher.invoke() })),
    // Нужно будет инжектить Hiltom
    viewModel: ChatViewModel,
    internalEvent: InternalEvent,
    tryLoadFileAction: (RoomMessage.SendingMessage) -> Unit,
    tryToDownLoadAction: (RoomMessage.SimpleMessage) -> Unit,
    applyMessageAction: (MessageWithFile) -> Unit,
) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val roomMessages = viewModel.messages.collectAsState() //{ it.roomID == roomID }.roomMessages

    Log.d("Chat", "Messages $roomMessages")

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
                    IconButton(onClick = { clearChat() }) {
                        Icon(Icons.Filled.ClearAll, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { innerPadding ->
        ChatTheme {
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
                sheetState = modalBottomSheetState)
            {
                val (message, messageChange) = rememberSaveable { mutableStateOf("") }
                val listState = rememberLazyListState()
                val fileUri = remember { mutableStateOf<Uri?>(null) }
                val filePath = remember { mutableStateOf<String?>(null) }
                val (openDialog, openDialogChange) = remember { mutableStateOf(false) }

                openDialogChange(if (internalEvent is InternalEvent.OpenFilePreview) {
                    fileUri.value = internalEvent.fileUri
                    filePath.value = internalEvent.filePath
                    internalEvent.openDialog
                } else {
                    false
                })

                if (openDialog) {
                    FilePreviewDialog(
                        openDialog = openDialogChange,
                        filePath = filePath.value,
                        fileUri = fileUri.value,
                        messageID = roomMessages.value.last().messageID + 1,
                        roomID = roomID,
                        applyMessage = applyMessageAction,
                        viewModel = viewModel)
                }

                if (listState.firstVisibleItemIndex >= roomMessages.value.size - 1) {
                    eventChatRead(ChatRead(roomId = roomID))
                }

                val sendAction = {
                    sendMessage(SendMessage(roomId = roomID,
                        text = message,
                        attachmentId = null))
                    messageChange("")
                }

                if (internalEvent is InternalEvent.OpenFilePreview) {
                    openDialogChange(true)
                }

                Column() {
                    LazyColumn(modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .padding(innerPadding), state = listState) {
                        items(roomMessages.value) { data ->
                            MessageItem(modifier = Modifier.padding(all = 4.dp),
                                message = data,
                                deleteMessageAction = deleteMessageAction,
                                tryLoadAction = tryLoadFileAction,
                                tryDownloadAction = tryToDownLoadAction
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(all = 4.dp)) {
                        Row() {
                            TextField(modifier = Modifier,
                                value = message,
                                onValueChange = messageChange)
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

                        Button(onClick = { sendAction() },
                            modifier = modifier.fillMaxWidth(),
                            enabled = message.isNotEmpty()) {
                            Text(text = "Отправить")
                        }
                    }

                }
            }

        }
    }

}

@Composable
fun openFilePreviewDialog(
    openDialog: MutableState<Boolean>,
    fileUri: Uri?,
    filePath: String?,
    applyMessage: (MessageWithFile) -> Unit,
) {
    ChatTheme {
        FilePreviewDialog(
            fileUri = fileUri,
            applyMessage = applyMessage,
            openDialog = { },
            filePath = filePath,
            viewModel = viewModel(),
            roomID = -1,
            messageID = -1)
    }
}

@Preview
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





