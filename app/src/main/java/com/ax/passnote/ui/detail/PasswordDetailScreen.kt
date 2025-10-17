package com.ax.passnote.ui.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ax.passnote.PassNoteApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailScreen(
    application: PassNoteApplication,
    passwordId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit // New callback to navigate to edit screen
) {
    val viewModel: PasswordDetailViewModel = viewModel(factory = PasswordDetailViewModelFactory(application.passwordRepository, passwordId))
    val password by viewModel.password.collectAsState()
    val showDeleteDialog by viewModel.showDeleteConfirmDialog.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("密码详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            password?.let { pw ->
                OutlinedTextField(value = pw.service, onValueChange = {}, label = { Text("公司/服务") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = pw.username, onValueChange = {}, label = { Text("用户名") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = pw.password, onValueChange = {}, label = { Text("密码") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = pw.notes ?: "", onValueChange = {}, label = { Text("备注") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(Modifier.height(24.dp))

                val buttonModifier = Modifier.width(280.dp)

                Button(onClick = { 
                    clipboardManager.setText(AnnotatedString(pw.password))
                    Toast.makeText(context, "密码已复制", Toast.LENGTH_SHORT).show()
                }, modifier = buttonModifier) {
                    Text("复制密码")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onNavigateToEdit() }, modifier = buttonModifier) { // Activate the edit button
                    Text("修改密码")
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.onShowDeleteDialog() }, 
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("删除密码")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("确认删除") },
            text = { Text("您确定要删除这条密码记录吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePassword()
                    viewModel.onDismissDeleteDialog()
                    onNavigateBack()
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("取消")
                }
            }
        )
    }
}