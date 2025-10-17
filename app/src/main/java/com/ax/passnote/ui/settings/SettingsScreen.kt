package com.ax.passnote.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ax.passnote.PassNoteApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    application: PassNoteApplication,
    onNavigateBack: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(application.settingsRepository))
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(16.dp)) {
            
            // Username Threshold
            SettingItem(
                title = "用户名提示阈值",
                value = settings.usernameThreshold.toString()
            ) {
                Slider(
                    value = settings.usernameThreshold.toFloat(),
                    onValueChange = { viewModel.onUsernameThresholdChange(it) },
                    valueRange = 3f..10f,
                    steps = 6
                )
            }

            Spacer(Modifier.height(16.dp))

            // Password Length
            SettingItem(
                title = "随机密码长度",
                value = settings.passwordLength.toString()
            ) {
                Slider(
                    value = settings.passwordLength.toFloat(),
                    onValueChange = { viewModel.onPasswordLengthChange(it) },
                    valueRange = 10f..20f,
                    steps = 9
                )
            }
            
            Spacer(Modifier.height(16.dp))

            // Use Custom Chars
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = settings.useCustomChars,
                    onCheckedChange = { viewModel.onUseCustomCharsChange(it) }
                )
                Spacer(Modifier.width(8.dp))
                Text("使用特殊字符") // Changed label
            }

            // Custom Chars Input
            if (settings.useCustomChars) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = settings.customChars,
                    onValueChange = { viewModel.onCustomCharsChange(it) },
                    label = { Text("自定义字符列表") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SettingItem(title: String, value: String, content: @Composable () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
        content()
    }
}
