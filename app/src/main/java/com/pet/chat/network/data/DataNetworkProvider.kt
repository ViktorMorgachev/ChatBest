package com.pet.chat.network.data

import com.pet.chat.App
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.data.base.User
import com.pet.chat.network.data.send.UserAuth
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.providers.interfaces.EventFromServerProvider
import com.pet.chat.providers.interfaces.UsersProvider
import com.pet.chat.ui.screens.chats.toChatItemInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataNetworkProvider @Inject constructor(val eventFromServerProvider: EventFromServerProvider,
                                              val connectionManager: ConnectionManager,
                                              val chatProvider: MultipleChatProviderImpl,
                                              val usersProviderImpl: UsersProvider<User>) {


    fun observe(applicationScope: CoroutineScope) {
        applicationScope.launch(Dispatchers.IO) {
            eventFromServerProvider.events.collect { eventFromServer->
                when(eventFromServer){
                    is EventFromServer.AutorizationEvent->{
                        val data = eventFromServer.data
                        val chats = data.dialogs.map{it.toChatItemInfo()}
                        App.prefs?.saveUser(UserAuth(data.user.id, token = data.token!!))
                        chats.forEach { chatItemInfo->
                            chatProvider.updateChat(chatItemInfo)
                        }
                    }
                    is EventFromServer.ConnectionSuccess->{
                       /* if (App.prefs?.identified() == true) {
                            connectionManager.postEventToServer(EventToServer.AuthEvent(UserAuth(id = App.prefs?.userID!!, token = App.prefs?.userToken!!)), error = {})
                            if (App.states?.lastRooom != -1) {
                                connectionManager.postEventToServer( EventToServer.GetChatHistory(ChatHistory(limit = 10, roomId = App.states?.lastRooom!!, lastId = null)), error = {})
                            }
                        }*/
                    }
                    is EventFromServer.MessageNewEvent ->{
                        val data = eventFromServer.data
                        chatProvider.updateChat(data.toChatItemInfo())
                    }
                    is EventFromServer.ChatHistoryEvent -> {
                        val data = eventFromServer.data
                        data.toChatItemInfo()?.let { chatItemInfo ->
                            chatProvider.updateChat(chatItemInfo)
                        }
                    }
                    is EventFromServer.ChatDeleteEvent ->{
                        val data = eventFromServer.data
                        chatProvider.deleteChat(chatID = data.room.id.toInt())
                    }
                    is EventFromServer.ChatClearEvent->{
                        val data = eventFromServer.data
                        chatProvider.clearChat(chatID = data.room.id.toInt())
                    }
                    is EventFromServer.MessageDeleteEvent->{
                        val data = eventFromServer.data
                        chatProvider.deleteMessageByID(messageID = data.message.id.toInt(), roomID = data.room.id.toInt())
                    }
                    is EventFromServer.ChatReadEvent->{
                        val data = eventFromServer.data
                        chatProvider.updateChatState(data)
                    }
                    is EventFromServer.UserOnlineEvent -> {
                        val data = eventFromServer.data
                        chatProvider.updateChatState(data)
                    }
                }
            }
        }
    }

}