package com.pet.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pet.chat.R
import com.pet.chat.network.data.send.DeleteMessage


val mockDataBottomSheetItem = BottomActionData(image = Icons.Outlined.Camera, "Camera", {})

val mockAliceMessage =
    RoomMessage(userID = "Alice",
        date = "12.12.2021",
        text = "From Alice",
        isOwn = false,
        messageID = -1)
val mockBobMessage =
    RoomMessage(userID = "Bob",
        date = "12.12.2021",
        text = "From Bob",
        isOwn = true,
        messageID = -1)

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

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: RoomMessage,
    deleteMessage: (DeleteMessage) -> Unit,
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
                                        deleteMessage(DeleteMessage(message.messageID))
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
                Text(text = message.text,
                    style = TextStyle.Default,
                    modifier = modifier
                        .padding(horizontal = 4.dp))
                Row {
                    if (isMe) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(text = message.date,
                        style = TextStyle.Default,
                        modifier = modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }

}