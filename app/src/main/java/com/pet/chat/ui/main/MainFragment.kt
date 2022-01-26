package com.pet.chat.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pet.chat.ui.Screen
import com.pet.chat.ui.chatflow.chatFlow
import com.pet.chat.ui.theme.ChatTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * В этом фрагменте при запуске при старте будем отправлять запрос на авторизацию по сокетам пока посстоянно пока,
 * после будем делать чекать всё ли хорошо
 * Этот модуль в целом работает с привязкой
 * с определёной структурой и определёнымми данными которые обращаются с сокетом
 ***/

@AndroidEntryPoint
class MainFragment(val userID: String, val socketToken: String) : Fragment() {

    private var composeView: ComposeView? = null

    companion object {
        fun newInstance(userID: String, socketToken: String): MainFragment =
            MainFragment(userID, socketToken)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ChatTheme() {
                    Box(modifier = Modifier.fillMaxSize()){
                        val viewModel = hiltViewModel<ChatViewModel>()
                        viewModel.autorize(userID = userID.toInt(), socketToken = socketToken)
                        MyApp(viewModel = viewModel)
                    }
                }
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