package com.pet.chat.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.pet.chat.App
import com.pet.chat.ui.*
import com.pet.chat.ui.chatflow.chatFlow
import com.pet.chat.ui.theme.ChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    var resultAfterCamera: ((Boolean) -> Unit)? = null
    var resultAfterCameraPermission: ((Boolean) -> Unit)? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        resultAfterCamera?.invoke(it)
    }
    private val cameraPermissionContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            resultAfterCameraPermission!!.invoke(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        setContent {
            val viewModel = hiltViewModel<ChatViewModel>()
                ChatTheme {
                    MyApp(viewModel)
                }
            }

        Log.d(
            "DebugInfo: ",
            "User autentificated: ${MainChatModule.chatsPrefs?.identified()} Current Room ${MainChatModule.chatsPrefs?.lastRoom}"
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MyApp(viewModel: ChatViewModel) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Screen.Autorization.route) {
            chatFlow(navController, viewModel)
        }

        resultAfterCameraPermission = { granted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    granted -> {
                        viewModel.takePicture(this,
                            launchCamera = { cameraLauncher.launch(it) })
                    }
                    !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                        // доступ к камере запрещен, пользователь поставил галочку Don't ask again.
                    }
                    else -> {
                        // доступ к камере запрещен, пользователь отклонил запрос
                    }
                }
            } else {
                viewModel.takePicture(this, launchCamera = { cameraLauncher.launch(it) })
            }
        }

        viewModel.cameraPermissionContract = this.cameraPermissionContract

        resultAfterCamera = {
            viewModel.resultAfterCamera(it)
        }

    }

}







