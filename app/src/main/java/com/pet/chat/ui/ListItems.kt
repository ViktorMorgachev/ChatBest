package com.pet.chat.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pet.chat.R
import com.pet.chat.network.data.base.File
import com.pet.chat.ui.theme.ChatTheme

val mockDataBottomSheetItem = BottomActionData(image = Icons.Outlined.Camera, "Camera", {})

val mockAliceMessage =
    RoomMessage.SimpleMessage(
        userID = "Alice",
        date = "12.12.2021",
        text = "From Alice",
        isOwn = false,
        messageID = -1,
        file = null)
val mockBobMessage =
    RoomMessage.SimpleMessage(
        userID = "Bob",
        date = "12.12.2021",
        text = "From Bob",
        isOwn = true,
        messageID = -1,
        file = File(
            roomID = -1,
            type = "photo",
            fileID = 213234,
            filePath = null, state = State.None))

@Composable
fun BottomSheetItem(itemData: BottomActionData, closeBottomAction: () -> Unit) {
    Column(modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {
                itemData.onClickAction.invoke()
                closeBottomAction()
            },
            modifier = Modifier
                .height(32.dp)
                .width(32.dp),
        ) {
            Icon(imageVector = itemData.image,
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp),
                contentDescription = itemData.itemDescribe)
        }

        Text(text = itemData.itemDescribe)
    }
}

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun MessageItemPreview(modifier: Modifier = Modifier) {
    ChatTheme {
        MessageItem(message = mockBobMessage,
            deleteMessageAction = {},
            tryUploadAction = {},
            tryDownloadAction = {})
    }
}

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: RoomMessage,
    deleteMessageAction: (RoomMessage) -> Unit,
    tryUploadAction: (RoomMessage.SendingMessage) -> Unit,
    tryDownloadAction: (RoomMessage.SimpleMessage) -> Unit,
) {
    val isMe = message.isOwn
    var expandedMenu by remember() { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        if (isMe) {
            Spacer(modifier = Modifier.weight(1f))
        }
        Card(modifier = modifier.fillMaxSize(0.7f)) {
            Column() {
                Row {
                    if (isMe) {
                        IconButton(onClick = { expandedMenu = !expandedMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            Text(text = stringResource(id = R.string.delete),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        deleteMessageAction(message)
                                        expandedMenu = false
                                    }))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(text = message.userID,
                        Modifier
                            .padding(4.dp)
                            .align(Alignment.CenterVertically))
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    when (message) {
                        is RoomMessage.SendingMessage -> {
                            message.file?.let { file ->
                                if (file.type == "photo") {
                                    Image(bitmap = BitmapFactory.decodeFile(file.filePath)
                                        .asImageBitmap(),
                                        contentDescription = "Image",
                                        modifier = Modifier.fillMaxWidth())
                                }
                                when (file.state) {
                                    State.Loading -> {
                                        CircularProgressIndicator(modifier = Modifier
                                            .fillMaxSize(0.5f)
                                            .padding(4.dp), strokeWidth = 4.dp)
                                    }
                                    State.Error -> {
                                        Button(
                                            onClick = { tryUploadAction(message) },
                                            modifier = Modifier
                                                .fillMaxSize(0.5f)
                                                .padding(4.dp)) {
                                            Text(text = "Заново отправить")
                                        }
                                    }
                                    State.Done -> {
                                        Icon(modifier = Modifier
                                            .fillMaxSize(0.5f)
                                            .padding(4.dp),
                                            imageVector = ImageVector.vectorResource(R.drawable.done),
                                            contentDescription = "Done")
                                    }
                                    else -> {}
                                }
                            }

                        }
                        is RoomMessage.SimpleMessage -> {
                            message.file?.let { attachment ->
                                when (attachment.state) {
                                    State.Loading -> {
                                        CircularProgressIndicator(modifier = Modifier
                                            .fillMaxSize(0.5f)
                                            .padding(4.dp))
                                        if (attachment.type == "photo") {
                                            Icon(imageVector = Icons.Default.PhotoCamera,
                                                contentDescription = "PhotoLoad",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(20.dp))
                                        }
                                    }
                                    State.Error -> {
                                        Button(
                                            onClick = { tryDownloadAction(message) },
                                            modifier = Modifier
                                                .fillMaxSize(0.5f)
                                                .padding(4.dp)) {
                                            Text(text = "Заново отправить")
                                        }
                                    }
                                    State.Loaded -> {
                                        if (attachment.filePath != null) {
                                            Icon(bitmap = BitmapFactory.decodeFile(attachment.filePath)
                                                .asImageBitmap(),
                                                contentDescription = "PhotoLoad",
                                                modifier = Modifier
                                                    .fillMaxSize(0.5f)
                                                    .padding(4.dp)
                                                    .height(20.dp))
                                        }
                                    }
                                    State.None -> {
                                        IconButton(onClick = { tryDownloadAction(message) }) {
                                            Icon(modifier = Modifier
                                                .fillMaxSize(0.5f)
                                                .padding(4.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.arrow_downward),
                                                contentDescription = "Download")
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                Text(text = message.text,
                    style = TextStyle.Default,
                    modifier = modifier
                        .padding(horizontal = 4.dp))
                Row {
                    if (isMe) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(text = "${message.date}",
                        style = TextStyle.Default,
                        modifier = modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }

}