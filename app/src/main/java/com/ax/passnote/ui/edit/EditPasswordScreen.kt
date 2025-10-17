package com.ax.passnote.ui.edit

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun EditPasswordScreen(
    application: PassNoteApplication,
    passwordId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: EditPasswordViewModel = viewModel(factory = EditPasswordViewModelFactory(application.passwordRepository, application.settingsRepository, passwordId))
    val passwordState by viewModel.password.collectAsState()
    val showConfirmDialog by viewModel.showConfirmDialog.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("修改密码") },
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
            passwordState?.let { password ->
                OutlinedTextField(value = password.service, onValueChange = {}, label = { Text("公司/服务") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = password.username, onValueChange = {}, label = { Text("用户名") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password.password,
                    onValueChange = { newPass -> if (newPass.length <= 20) viewModel.onPasswordChange(newPass) }, 
                    label = { Text("密码") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password.notes ?: "", 
                    onValueChange = { newNotes -> viewModel.onNotesChange(newNotes) }, 
                    label = { Text("备注") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                val buttonModifier = Modifier.width(280.dp)

                Button(onClick = { viewModel.generateRandomPassword() }, modifier = buttonModifier) {
                    Text("随机生成密码")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { 
                    clipboardManager.setText(AnnotatedString(password.password))
                    Toast.makeText(context, "密码已复制", Toast.LENGTH_SHORT).show()
                }, modifier = buttonModifier) {
                    Text("复制当前密码")
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.onShowConfirmDialog() }, modifier = buttonModifier) {
                    Text("确认修改")
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissConfirmDialog() },
            title = { Text("确认修改") },
            text = { Text("即将覆盖原密码，是否继续？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updatePassword()
                    viewModel.onDismissConfirmDialog()
                    onNavigateBack()
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissConfirmDialog() }) {
                    Text("取消")
                }
            }
        )
    }
}