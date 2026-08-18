package com.nativemediaplayer.ui.screens

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LargeFileItem(val id: Long, val uri: android.net.Uri, val name: String, val size: Long, val path: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeFileScannerScreen() {
    val context = LocalContext.current
    var files by remember { mutableStateOf<List<LargeFileItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var fileToDelete by remember { mutableStateOf<LargeFileItem?>(null) }

    val deleteLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            fileToDelete?.let { files = files.filter { it.id != fileToDelete?.id } }
        }
    }

    suspend fun scanLargeFiles(): List<LargeFileItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<LargeFileItem>()
        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATA)
        val sel = "${MediaStore.Files.FileColumns.SIZE} > ?"
        val args = arrayOf((100L*1024*1024).toString())
        val uri = MediaStore.Files.getContentUri("external")
        context.contentResolver.query(uri, projection, sel, args, "${MediaStore.Files.FileColumns.SIZE} DESC")?.use { cursor ->
            val idC = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameC = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeC = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dataC = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idC)
                list.add(LargeFileItem(id, ContentUris.withAppendedId(uri, id), cursor.getString(nameC) ?: "Unknown", cursor.getLong(sizeC), cursor.getString(dataC) ?: ""))
            }
        }
        list
    }

    LaunchedEffect(Unit) {
        files = scanLargeFiles()
        loading = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Large Files >100MB") }) }) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(files) { file ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Folder, null, Modifier.size(40.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(file.name, maxLines = 1, style = MaterialTheme.typography.titleSmall)
                                Text(file.path, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                                Text("${file.size / (1024*1024)} MB", color = MaterialTheme.colorScheme.error)
                            }
                            IconButton(onClick = {
                                fileToDelete = file
                                try {
                                    context.contentResolver.delete(file.uri, null, null)
                                    files = files.filter { it.id != file.id }
                                } catch (e: SecurityException) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        val rec = e as? RecoverableSecurityException
                                        rec?.let {
                                            deleteLauncher.launch(IntentSenderRequest.Builder(it.userAction.actionIntent.intentSender).build())
                                        }
                                    }
                                }
                            }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}
