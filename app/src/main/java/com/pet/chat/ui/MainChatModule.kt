package com.pet.chat.ui


import android.content.Context
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.data.DataNetworkProvider
import com.pet.chat.storage.ChatsPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class MainChatModule @Inject constructor(val dataNetworkProvider: DataNetworkProvider, val connectionManager: ConnectionManager
) {

    companion object{
        var chatsPrefs: ChatsPrefs? = null
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun initialize(context: Context) {
        chatsPrefs = ChatsPrefs(context)
        dataNetworkProvider.observe(applicationScope)
        connectionManager.initConnection()
    }


}