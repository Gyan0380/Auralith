package com.nativemediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.nativemediaplayer.ui.screens.*
import com.nativemediaplayer.ui.components.MiniPlayerBar
import com.nativemediaplayer.service.PlayerService

enum class Screen { Home, Music, Video, Files, Apps, FullPlayer }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NativeMediaPlayerApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NativeMediaPlayerApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var isFullPlayer by remember { mutableStateOf(false) }
    var videoUri by remember { mutableStateOf("") }

    MaterialTheme(colorScheme = lightColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF6C4DFF))) {
        Scaffold(
            bottomBar = {
                Column {
                    MiniPlayerBar(onExpand = { isFullPlayer = true })
                    NavigationBar {
                        NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = currentScreen == Screen.Home, onClick = { currentScreen = Screen.Home; isFullPlayer = false })
                        NavigationBarItem(icon = { Icon(Icons.Default.MusicNote, null) }, label = { Text("Music") }, selected = currentScreen == Screen.Music, onClick = { currentScreen = Screen.Music; isFullPlayer = false })
                        NavigationBarItem(icon = { Icon(Icons.Default.VideoLibrary, null) }, label = { Text("Video") }, selected = currentScreen == Screen.Video, onClick = { currentScreen = Screen.Video; isFullPlayer = false })
                        NavigationBarItem(icon = { Icon(Icons.Default.Folder, null) }, label = { Text("Files") }, selected = currentScreen == Screen.Files, onClick = { currentScreen = Screen.Files; isFullPlayer = false })
                        NavigationBarItem(icon = { Icon(Icons.Default.Apps, null) }, label = { Text("Apps") }, selected = currentScreen == Screen.Apps, onClick = { currentScreen = Screen.Apps; isFullPlayer = false })
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                AnimatedContent(
                    targetState = Pair(currentScreen, isFullPlayer),
                    transitionSpec = {
                        val (curr, full) = targetState
                        val (prev, prevFull) = initialState
                        when {
                            full -> slideInVertically(tween(400)) { it } + fadeIn() togetherWith slideOutVertically(tween(200)) { -it/5 } + fadeOut()
                            prevFull -> slideInVertically(tween(300)) { -it/5 } togetherWith slideOutVertically(tween(400)) { it } + fadeOut()
                            else -> {
                                val order = listOf(Screen.Home, Screen.Music, Screen.Video, Screen.Files, Screen.Apps)
                                val cIdx = order.indexOf(curr)
                                val pIdx = order.indexOf(prev)
                                if (cIdx > pIdx) slideInHorizontally(tween(350)) { it } + fadeIn(tween(350)) togetherWith slideOutHorizontally(tween(350)) { -it/3 } + fadeOut(tween(200))
                                else slideInHorizontally(tween(350)) { -it } + fadeIn(tween(350)) togetherWith slideOutHorizontally(tween(350)) { it/3 } + fadeOut(tween(200))
                            }
                        }
                    }
                ) { (screen, full) ->
                    if (full) {
                        FullPlayerScreen(videoUri = videoUri, onClose = { isFullPlayer = false })
                    } else {
                        when (screen) {
                            Screen.Home -> HomeScreen(onNavigate = { currentScreen = it })
                            Screen.Music -> MusicScreen(onPlay = { uri -> videoUri = uri; isFullPlayer = true })
                            Screen.Video -> VideoLibraryScreen(onPlay = { uri -> videoUri = uri; isFullPlayer = true })
                            Screen.Files -> LargeFileScannerScreen()
                            Screen.Apps -> AppManagerScreen()
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
