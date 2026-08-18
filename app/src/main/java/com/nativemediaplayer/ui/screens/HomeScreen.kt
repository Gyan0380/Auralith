package com.nativemediaplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nativemediaplayer.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Media Player & File Manager", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item { HomeTile("Music", Icons.Default.MusicNote) { onNavigate(Screen.Music) } }
            item { HomeTile("Videos", Icons.Default.VideoLibrary) { onNavigate(Screen.Video) } }
            item { HomeTile("Large Files", Icons.Default.Folder) { onNavigate(Screen.Files) } }
            item { HomeTile("App Manager", Icons.Default.Apps) { onNavigate(Screen.Apps) } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.height(120.dp)) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Icon(icon, null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(title)
        }
    }
}
