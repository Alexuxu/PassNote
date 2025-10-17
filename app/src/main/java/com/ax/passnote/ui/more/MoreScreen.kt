package com.ax.passnote.ui.more

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ax.passnote.PassNoteApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    application: PassNoteApplication,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: MoreViewModel = viewModel(factory = MoreViewModelFactory(application.passwordRepository))
    val context = LocalContext.current
    val showClearConfirmDialog by viewModel.showClearConfirmDialog.collectAsState()
    val showCsvErrorDialog by viewModel.showCsvErrorDialog.collectAsState()
    val importSuccess by viewModel.importSuccess.collectAsState()
    val exportSuccess by viewModel.exportSuccess.collectAsState()
    val clearSuccess by viewModel.clearSuccess.collectAsState()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                viewModel.importPasswordsFromCsv(it, context.contentResolver)
            }
        }
    )

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.exportPasswordsToCsv(it, context.contentResolver) }
        }
    )

    LaunchedEffect(importSuccess) {
        if (importSuccess) {
            Toast.makeText(context, "导入成功", Toast.LENGTH_SHORT).show()
            viewModel.onImportSuccessToastShown()
        }
    }

    LaunchedEffect(exportSuccess) {
        if (exportSuccess) {
            Toast.makeText(context, "导出成功", Toast.LENGTH_SHORT).show()
            viewModel.onExportSuccessToastShown()
        }
    }
    
    LaunchedEffect(clearSuccess) {
        if (clearSuccess) {
            Toast.makeText(context, "数据已清除", Toast.LENGTH_SHORT).show()
            viewModel.onClearSuccessToastShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("更多功能") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val buttonModifier = Modifier.width(280.dp)
            
            Button(onClick = { exportLauncher.launch("passnote.csv") }, modifier = buttonModifier) {
                Icon(Icons.Default.GetApp, contentDescription = "导出图标", modifier = Modifier.padding(end = 8.dp))
                Text("导出到 PassNote.csv")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "application/vnd.ms-excel")) }, modifier = buttonModifier) {
                Icon(Icons.Default.Publish, contentDescription = "导入图标", modifier = Modifier.padding(end = 8.dp))
                Text("从文件导入密码")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.onShowClearConfirmDialog() },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = "清除图标", modifier = Modifier.padding(end = 8.dp))
                Text("一键清除所有数据")
            }
        }
    }

    // --- Dialogs ---
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissClearConfirmDialog() },
            title = { Text("确认操作") },
            text = { Text("是否清除全部数据？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    viewModel.onDismissClearConfirmDialog()
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissClearConfirmDialog() }) {
                    Text("取消")
                }
            }
        )
    }

    if (showCsvErrorDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissCsvErrorDialog() },
            title = { Text("导入失败") },
            text = { Text("文件格式不正确。请确保文件为 CSV 格式，且包含 'Company', 'Username', 'Password' 这三列。") },
            confirmButton = {
                TextButton(onClick = { viewModel.onDismissCsvErrorDialog() }) {
                    Text("好的")
                }
            }
        )
    }
}
