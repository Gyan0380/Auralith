package com.nativemediaplayer.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.app.usage.StorageStatsManager

data class AppInfo(val packageName: String, val appName: String, val sizeBytes: Long)

class AppManagerHelper(private val context: Context) {
    suspend fun getUserApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val statsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager else null
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .map { app ->
                val size = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val uuid = statsManager?.getUuidForPath(context.filesDir)
                        val stats = uuid?.let { u -> statsManager?.queryStatsForPackage(u, app.packageName, android.os.Process.myUserHandle()) }
                        stats?.appBytes ?: 0L
                    } else 0L
                } catch (e: Exception) { 0L }
                AppInfo(app.packageName, pm.getApplicationLabel(app).toString(), size)
            }.sortedByDescending { it.sizeBytes }
    }
    fun openUninstall(pkg: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.parse("package:$pkg")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppManagerScreen() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        apps = AppManagerHelper(context).getUserApps()
        loading = false
    }
    Scaffold(topBar = { TopAppBar(title = { Text("App Manager") }) }) { padding ->
        if (loading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(apps) { app ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(app.appName, style = MaterialTheme.typography.titleMedium)
                            Text(app.packageName, style = MaterialTheme.typography.bodySmall)
                            Text("${app.sizeBytes / (1024*1024)} MB", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { AppManagerHelper(context).openUninstall(app.packageName) }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            }
        }
    }
}
fun formatSize(bytes: Long): String = "${bytes/(1024*1024)} MB"
