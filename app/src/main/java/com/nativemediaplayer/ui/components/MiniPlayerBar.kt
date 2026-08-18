package com.nativemediaplayer.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nativemediaplayer.service.PlayerService

@Composable
fun MiniPlayerBar(onExpand: () -> Unit) {
    val state by PlayerService.state.collectAsState()
    AnimatedVisibility(
        visible = state.uri.isNotEmpty(),
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Surface(tonalElevation = 6.dp, modifier = Modifier.fillMaxWidth().clickable { onExpand() }) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(state.title.ifEmpty { "Now Playing" }, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                    LinearProgressIndicator(progress = state.progress, modifier = Modifier.fillMaxWidth(0.5f).padding(top=4.dp))
                }
                IconButton(onClick = { PlayerService.setPlaying(!state.isPlaying) }) {
                    Icon(if(state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                }
            }
        }
    }
}
