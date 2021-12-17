package com.pet.chat.ui

import android.net.Uri
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
import com.pet.chat.events.InternalEvent
import com.pet.chat.network.data.Attachment
import com.pet.chat.network.data.Message
import com.pet.chat.network.data.send.ChatRead
import com.pet.chat.network.data.send.DeleteMessage
import com.pet.chat.network.data.send.SendMessage
import com.pet.chat.ui.main.ChatViewModel
import com.pet.chat.ui.theme.LoveFinderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class BottomActionData(
    val image: ImageVector,
    val itemDescribe: String,
    val onClickAction: () -> Unit,
)

data class RoomMessage(
    val messageID: Int,
    val userID: String,
    val date: String,
    val text: String,
    val isOwn: Boolean = false,
)

fun Message.toRoomMessage(): RoomMessage {
    return RoomMessage(userID = user_id.toString(),
        date = created_at.toString(),
        text = text,
        isOwn = App.prefs?.userID == user_id.toInt(), messageID = this.id.toInt())
}

val mockData: List<RoomMessage> = listOf(mockAliceMessage.copy(text = "Hi Bob"),
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
    LoveFinderTheme {
        val dataForTesting = listOf(
            BottomActionData(image = Icons.Outlined.Camera,
                itemDescribe = "Camera",
                onClickAction = {}))
        Chat(
            sendMessage = {},
            roomID = -1,
            clearChat = {},
            messages = mockData,
            navController = null,
            deleteMessage = { },
            eventChatRead = {},
            loadFileAction = {},
            scope = rememberCoroutineScope(),
            cameraLauncher = { }, viewModel = viewModel()
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Chat(
    modifier: Modifier = Modifier,
    sendMessage: (SendMessage) -> Unit,
    roomID: Int,
    clearChat: () -> Unit,
    messages: List<RoomMessage>,
    navController: NavController?,
    deleteMessage: (DeleteMessage) -> Unit,
    eventChatRead: (ChatRead) -> Unit,
    loadFileAction: (Attachment) -> Unit,
    scope: CoroutineScope,
    cameraLauncher: () -> Unit,
    bottomSheetActions: List<BottomActionData> =
        listOf(BottomActionData(image = Icons.Outlined.Camera,
            itemDescribe = "Camera",
            onClickAction = { cameraLauncher.invoke() })),
    // Нужно будет инжектить Hiltom
    viewModel: ChatViewModel,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )


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
        LoveFinderTheme {
            ModalBottomSheetLayout(
                sheetContent = {
                    LoveFinderTheme {
                        LazyRow() {
                            items(bottomSheetActions) { item ->
                                BottomSheetItem(itemData = item, closeBottomAction = {
                                    scope.launch {
                                        modalBottomSheetState.hide()
                                    }
                                })
                            }
                        }
                    }
                },
                sheetState = modalBottomSheetState)
            {
                val (message, messageChange) = rememberSaveable { mutableStateOf("") }
                val listState = rememberLazyListState()
                val openDialog = remember { mutableStateOf(false) }
                val internalEvents = viewModel.internalEvents.collectAsState()
                val fileUri = remember {
                    if (internalEvents.value is InternalEvent.OpenFilePreview) {
                        (internalEvents.value as InternalEvent.OpenFilePreview).file
                    } else null
                }

                if (internalEvents.value is InternalEvent.OpenFilePreview) {
                    openDialog.value = true
                }
                openFilePreviewDialog(openDialog = openDialog,
                    fileUri = fileUri,
                    applyMessage = { message, fileUri ->
                        // TODO add message with loading image progress
                    })

                if (listState.firstVisibleItemIndex >= messages.size - 1) {
                    eventChatRead(ChatRead(roomId = roomID))
                }
                val sendAction = {
                    sendMessage(SendMessage(roomId = roomID,
                        text = message,
                        attachmentId = null))
                    messageChange("")
                }
                Column() {
                    LazyColumn(modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .padding(innerPadding), state = listState) {
                        items(messages) { data ->
                            MessageItem(modifier = Modifier.padding(all = 4.dp),
                                message = data,
                                deleteMessage)
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
    applyMessage: (message: String, fileUri: Uri) -> Unit,
) {
    LoveFinderTheme {
        FilePreviewDialog(fileUri = fileUri, applyMessage = applyMessage, openDialog = openDialog)
    }
}

@Preview
@Composable
fun BottomSheetItemPreview() {
    LoveFinderTheme {
        LazyRow() {
            items(mockDataBottomSheetItems) { item ->
                BottomSheetItem(itemData = item, closeBottomAction = {})
            }
        }
    }
}





