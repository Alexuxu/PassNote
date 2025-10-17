package com.ax.passnote.ui.add

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ax.passnote.PassNoteApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    application: PassNoteApplication,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val viewModel: AddPasswordViewModel = viewModel(factory = AddPasswordViewModelFactory(application.passwordRepository, application.settingsRepository))
    val validationError by viewModel.validationError.collectAsState()
    val frequentUsernames by viewModel.frequentUsernames.collectAsState()

    var service by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加新密码") },
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
            horizontalAlignment = Alignment.CenterHorizontally // Center buttons
        ) {
            OutlinedTextField(value = service, onValueChange = { service = it }, label = { Text("公司/服务*") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            // --- Intelligent Username Field ---
            if (frequentUsernames.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("用户名") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        frequentUsernames.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.username) },
                                onClick = {
                                    username = user.username
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("用户名") }, modifier = Modifier.fillMaxWidth())
            }
            // --- End of Intelligent Username Field ---

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { if (it.length <= 20) password = it }, label = { Text("密码*") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("备注") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))

            val buttonModifier = Modifier.width(280.dp) // Set a fixed width for buttons

            Button(onClick = { password = viewModel.generateRandomPassword() }, modifier = buttonModifier) {
                Text("随机生成密码")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { clipboardManager.setText(AnnotatedString(password)) }, modifier = buttonModifier) {
                Text("复制密码")
            }
            Spacer(Modifier.height(16.dp))

            Button(onClick = { viewModel.validateAndSave(service, username, password, notes) }, modifier = buttonModifier) {
                Text("保存")
            }
        }
    }

    if (validationError != null && validationError != "Success") {
        AlertDialog(
            onDismissRequest = { viewModel.resetValidationError() },
            title = { Text("校验失败") },
            text = { Text(validationError ?: "") },
            confirmButton = { TextButton(onClick = { viewModel.resetValidationError() }) { Text("好的") } }
        )
    }

    if (validationError == "Success") {
        onSaveSuccess()
        viewModel.resetValidationError() // Reset after navigating
    }
}
