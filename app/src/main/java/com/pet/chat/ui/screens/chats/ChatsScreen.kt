package com.pet.chat.ui.screens.chats

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pet.chat.R
import com.pet.chat.helpers.observeAsState
import com.pet.chat.helpers.retryAction
import com.pet.chat.network.data.ViewState
import com.pet.chat.network.data.base.Dialog
import com.pet.chat.network.data.receive.MessageNew
import com.pet.chat.network.data.send.ChatDelete
import com.pet.chat.network.data.send.ChatHistory
import com.pet.chat.ui.*
import com.pet.chat.ui.screens.chat.RoomMessage
import com.pet.chat.ui.screens.chat.toSimpleMessage
import com.pet.chat.ui.theme.ChatTheme
import com.pet.chat.ui.theme.Shapes



val mockRoomChat = ChatItemInfo(
    roomID = 122,
    usersIDs = listOf(145, 176),
    unreadCount = 7,
    roomMessages = mutableListOf()
)

val mockRoomChats = listOf(
    mockRoomChat,
    mockRoomChat.copy(roomID = 123, unreadCount = 2, usersIDs = listOf(145, 164))
)

val tag = "ChatsScreen"

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    deleteChat: (ChatDelete) -> Unit,
    navController: NavController,
    viewModel: ChatsViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val firstViewState = if(viewModel.chatProviderImpl.chats.value.isNotEmpty()){ ViewState.Display(listOf(viewModel.chatProviderImpl.chats.value)) } else ViewState.StateLoading
    val viewState = viewModel.viewStateProvider.viewState.observeAsState(firstViewState)

    SideEffect {
        Log.d(tag, "ViewState: ${viewState.value}")
        Log.d(tag, "Chats: ${viewModel.chatProviderImpl.chats.value}")
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.chats))
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("CreateChat")
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "?????????????? ??????")
            }
        }
    ) { innerPadding ->
            when (viewState.value) {
                is ViewState.StateLoading -> {
                    LoadingView(modifier = Modifier.padding(paddingValues = innerPadding))
                }
                is ViewState.Error -> {
                    ErrorView(
                        modifier = Modifier.padding(paddingValues = innerPadding),
                        retryAction = { retryAction.invoke() },
                        errorText = (viewState.value as ViewState.Error).errorInfo
                    )
                }
                is ViewState.StateNoItems -> {
                    NoItemsView(message = "?????????? ?????? ???? ???????????? ????????????", iconResID = null)
                }
                is ViewState.Display ->{
                    val firstItem = (viewState.value as ViewState.Display).data.firstOrNull() as List<*>
                    if (firstItem.firstOrNull() is ChatItemInfo){
                        val chats = firstItem as List<ChatItemInfo>
                        Log.d("ChatScreen", "Chats ${chats.size}")
                        DisplayChats(modifier = Modifier.padding(innerPadding), chats = chats, deleteChatAction = deleteChat, navController = navController)
                    }

                }
            }


    }

    DisposableEffect(key1 = viewModel){
        viewModel.onStart()
        onDispose {
            viewModel.onStop()
        }
    }

}

@Composable
fun DisplayChats(
    modifier: Modifier = Modifier,
    chats: List<ChatItemInfo>,
    deleteChatAction: (ChatDelete) -> Unit,
    navController: NavController) {

    SideEffect {
        Log.d("Screen", "ChatsView")
    }

    if(chats.isNotEmpty()){
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
        ) {
            items(chats) { item ->
                ChatsItem(chatDetails = item,
                    OpenChat = { chatHistory->
                        retryAction = {
                            navController.navigate(Screen.Room.createRoute(roomID = chatHistory.roomId.toString()))
                        }
                        retryAction()
                    },
                    deleteChat = {
                        retryAction = { deleteChatAction(it) }
                        retryAction()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatsItem(
    modifier: Modifier = Modifier,
    chatDetails: ChatItemInfo,
    deleteChat: (ChatDelete) -> Unit,
    OpenChat: (ChatHistory) -> Unit,
) {
    Card(
        shape = Shapes.medium,
        onClick = { OpenChat(ChatHistory(lastId = null, limit = 10, roomId = chatDetails.roomID)) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Row {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "RoomID: ${chatDetails.roomID}")
                Text(
                    text = "????????????????????????: ${chatDetails.usersIDs.joinToString()}",
                    modifier = Modifier.padding()
                )
                Text(text = "???????????????????? ???? ???????????????????? ??????????????????: ${chatDetails.roomID}")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { deleteChat(ChatDelete(roomId = chatDetails.roomID)) },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }

        }

    }
}

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun ChatsScreenPreview() {
    ChatTheme {
        ChatsScreen(deleteChat = {}, navController = rememberNavController(), viewModel = viewModel())
    }
}