package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DreamViewModel
import com.example.ui.ScreenStatus
import com.example.ui.screens.SomnioAuthScreen
import com.example.ui.screens.SomnioMainScaffold
import com.example.ui.screens.SomnioOnboarding
import com.example.ui.screens.SomnioSplash
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: DreamViewModel = viewModel()
                val screen by viewModel.screenStatus.collectAsStateWithLifecycle()
                var registerMode by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepSpaceBlack
                ) {
                    when (screen) {
                        ScreenStatus.SPLASH -> {
                            SomnioSplash()
                        }
                        ScreenStatus.ONBOARDING -> {
                            SomnioOnboarding(
                                onFinish = {
                                    viewModel.changeScreen(ScreenStatus.AUTH_LOGIN)
                                }
                            )
                        }
                        ScreenStatus.AUTH_LOGIN -> {
                            SomnioAuthScreen(
                                registerMode = false,
                                viewModel = viewModel,
                                onToggleMode = {
                                    viewModel.changeScreen(ScreenStatus.AUTH_REGISTER)
                                }
                            )
                        }
                        ScreenStatus.AUTH_REGISTER -> {
                            SomnioAuthScreen(
                                registerMode = true,
                                viewModel = viewModel,
                                onToggleMode = {
                                    viewModel.changeScreen(ScreenStatus.AUTH_LOGIN)
                                }
                            )
                        }
                        ScreenStatus.MAIN -> {
                            SomnioMainScaffold(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
