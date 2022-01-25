package com.pet.chat.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pet.chat.R
import com.pet.chat.ui.Screen
import com.pet.chat.ui.chatflow.chatFlow
import com.pet.chat.ui.theme.ChatTheme

/**
 * В этом фрагменте при запуске при старте будем отправлять запрос на авторизацию по сокетам пока посстоянно пока,
 * после будем делать чекать всё ли хорошо
 * Этот модуль в целом работает с привязкой
 * с определёной структурой и определёнымми данными которые обращаются с сокетом
 ***/
class MainFragment(val userID: String, val socketToken: String) : Fragment() {

    private var composeView: ComposeView? = null

    companion object {
        fun newInstance(userID: String, socketToken: String): MainFragment =
            MainFragment(userID, socketToken)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView = view.findViewById(R.id.composeView)
        setContent {
            val viewModel = hiltViewModel<ChatViewModel>()
            viewModel.autorize(userID = userID.toInt(), socketToken = socketToken)
            ChatTheme {
                MyApp(viewModel)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MyApp(viewModel: ChatViewModel) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Screen.Chats.route) {
            chatFlow(navController, viewModel)
        }
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeView?.setContent {
            content()
        }
    }
}