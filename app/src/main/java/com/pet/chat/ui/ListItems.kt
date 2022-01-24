package com.pet.chat.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.pet.chat.ui.screens.chat.BottomActionData
import com.pet.chat.ui.screens.chat.RoomMessage
import com.pet.chat.ui.screens.chat.State
import com.pet.chat.ui.theme.ChatTheme
import com.pet.chat.ui.theme.contentColor
import com.pet.chat.ui.theme.messageBackGround
import com.pet.chat.ui.theme.messageOwnBackGround

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
    message: RoomMessage,
    deleteMessageAction: (RoomMessage) -> Unit,
    tryUploadAction: (RoomMessage.SendingMessage) -> Unit,
    tryDownloadAction: (RoomMessage.SimpleMessage) -> Unit,
) {
    val isMe = message.isOwn
    var expandedMenu by remember() { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.padding(4.dp)) {
        if (isMe) {
            Spacer(modifier = Modifier.weight(1f))
        }
        val messageBackGround = remember {
            if(isMe) messageOwnBackGround else messageBackGround
        }
        Card(modifier = Modifier.fillMaxWidth(0.7f), contentColor = contentColor) {

            Column(modifier = Modifier.background(color = messageBackGround)) {
                Row {
                    if (isMe) {
                        IconButton(onClick = { expandedMenu = !expandedMenu }, modifier = Modifier.size(18.dp)) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            Text(text = stringResource(id = R.string.delete),
                                fontSize = 8.sp,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        deleteMessageAction(message)
                                        expandedMenu = false
                                    }))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                   Text(text = message.text, fontSize = 16.sp, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }

}