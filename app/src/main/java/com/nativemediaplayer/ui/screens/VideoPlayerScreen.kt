package com.nativemediaplayer.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.nativemediaplayer.service.PlayerService

// VIDEO PLAYER SURFACE - Media3/ExoPlayer Native
@Composable
fun FullPlayerScreen(videoUri: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            if (videoUri.isNotEmpty()) {
                setMediaItem(MediaItem.fromUri(Uri.parse(videoUri)))
                prepare()
                playWhenReady = true
            }
        }
    }
    var isPlaying by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Default.KeyboardArrowDown, null) }
            Text("Now Playing", style = MaterialTheme.typography.titleLarge)
        }
        Box(Modifier.fillMaxWidth().weight(1f)) {
            AndroidView(factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            }, modifier = Modifier.fillMaxSize())

            Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(), tonalElevation = 8.dp) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (exoPlayer.isPlaying) { exoPlayer.pause(); isPlaying = false; PlayerService.setPlaying(false) }
                        else { exoPlayer.play(); isPlaying = true; PlayerService.setPlaying(true) }
                    }) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, Modifier.size(40.dp))
                    }
                    var progress by remember { mutableStateOf(0f) }
                    LaunchedEffect(exoPlayer) {
                        while (true) {
                            val dur = exoPlayer.duration.takeIf { it > 0 } ?: 1L
                            progress = exoPlayer.currentPosition.toFloat() / dur
                            PlayerService.updateProgress(progress)
                            kotlinx.coroutines.delay(500)
                        }
                    }
                    Slider(value = progress, onValueChange = { exoPlayer.seekTo((it * exoPlayer.duration).toLong()) }, modifier = Modifier.weight(1f).padding(horizontal=12.dp))
                }
            }
        }
    }
}

@Composable
fun MusicScreen(onPlay: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Music Library - tap to play demo", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { onPlay("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4") }, modifier = Modifier.padding(top=16.dp)) {
            Text("Play Sample Video")
        }
    }
}

@Composable
fun VideoLibraryScreen(onPlay: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Video Library", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { onPlay("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4") }, modifier = Modifier.padding(top=16.dp)) {
            Text("Play Big Buck Bunny (Sample)")
        }
    }
}
