package com.pet.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.data.send.ChatDelete
import com.pet.chat.network.data.send.ChatHistory
import com.pet.chat.ui.theme.Shapes
import com.pet.chat.ui.theme.chatBackground
import com.pet.chat.ui.theme.chatSecondaryTextColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItemMaterial(modifier: Modifier = Modifier,
                     chatDetails: ChatItemInfo,
                     deleteChat: (ChatDelete) -> Unit,
                     openChat: (ChatHistory) -> Unit){

    Card(
        shape = Shapes.medium,
        onClick = { openChat(ChatHistory(lastId = null, limit = 10, roomId = chatDetails.roomID)) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = chatBackground)

    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(modifier = modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp)) {
                Image(
                    painter = painterResource(R.drawable.ic_account),
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "Пользователь ${chatDetails.usersIDs.first { it != MainChatModule.chatsPrefs?.userID }}", fontStyle = FontStyle.Normal, fontSize = 16.sp, modifier = Modifier.padding(bottom = 11.dp))
                    Text(text = chatDetails.roomMessages.lastOrNull()?.text ?: "", color = chatSecondaryTextColor, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "11/16/19", color = chatSecondaryTextColor, fontSize = 14.sp, modifier = Modifier.padding(end = 16.dp))
                IconButton(
                    onClick = { deleteChat(ChatDelete(roomId = chatDetails.roomID)) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(2.dp).background(chatSecondaryTextColor).padding(start = 84.dp))
        }


    }

}
